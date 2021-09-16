package nextstep.mvc.controller.tobe;

import jakarta.servlet.http.HttpServletRequest;
import nextstep.mvc.HandlerMapping;
import nextstep.mvc.exception.DuplicatedRequestMappingException;
import nextstep.web.annotation.RequestMapping;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.lang.reflect.Method;
import java.util.*;
import java.util.stream.Collectors;

public class AnnotationHandlerMapping implements HandlerMapping {

    private static final Logger log = LoggerFactory.getLogger(AnnotationHandlerMapping.class);

    private final Object[] basePackage;
    private final Map<HandlerKey, HandlerExecution> handlerExecutions;

    public AnnotationHandlerMapping(Object... basePackage) {
        this.basePackage = basePackage;
        this.handlerExecutions = new HashMap<>();
    }

    public void initialize() {
        log.info("Start Initialize AnnotationHandlerMapping!");

        ControllerScanner controllerScanner = new ControllerScanner(basePackage);
        Map<Class<?>, Object> controllers = controllerScanner.getControllers();
        controllers.forEach((aClass, instance) -> {
            log.debug("{} Class mapping ..", aClass.getName());
            List<Method> methods = getMethodsOfRequestMapping(aClass);
            addHandlerExecution(instance, methods);
        });

        log.info("Initialized AnnotationHandlerMapping!");
    }

    private List<Method> getMethodsOfRequestMapping(Class<?> aClass) {
        return Arrays.stream(aClass.getDeclaredMethods())
                .filter(method -> method.isAnnotationPresent(RequestMapping.class))
                .collect(Collectors.toList());
    }

    private void addHandlerExecution(Object instance, List<Method> methods) {
        for (Method method : methods) {
            RequestMapping requestMapping = method.getDeclaredAnnotation(RequestMapping.class);
            HandlerKey handlerKey = HandlerKey.of(requestMapping);
            checkExistsRequestMapping(handlerKey);
            this.handlerExecutions.put(handlerKey, new HandlerExecution(method, instance));
            log.info("Path : {}, Controller : class {}", requestMapping.value(), instance.getClass().getName());
        }
    }

    private void checkExistsRequestMapping(HandlerKey handlerKey) {
        if (this.handlerExecutions.containsKey(handlerKey)) {
            throw new DuplicatedRequestMappingException("HandlerMapping initialize failed! : Duplicated RequestMapping");
        }
    }

    public Object getHandler(HttpServletRequest request) {
        return handlerExecutions.get(HandlerKey.of(request));
    }
}
