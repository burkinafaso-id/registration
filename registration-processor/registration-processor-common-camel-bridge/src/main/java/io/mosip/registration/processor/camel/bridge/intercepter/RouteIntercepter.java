package io.mosip.registration.processor.camel.bridge.intercepter;

import java.util.List;

import org.apache.camel.CamelContext;
import org.apache.camel.Predicate;
import org.apache.camel.builder.AdviceWithRouteBuilder;
import org.apache.camel.model.RouteDefinition;
import org.springframework.beans.factory.annotation.Autowired;

import io.mosip.kernel.core.logger.spi.Logger;
import io.mosip.registration.processor.core.abstractverticle.MessageBusAddress;
import io.mosip.registration.processor.core.constant.LoggerFileConstant;
import io.mosip.registration.processor.core.logger.RegProcessorLogger;

public class RouteIntercepter {
	
	private static final Logger LOGGER = RegProcessorLogger.getLogger(RouteIntercepter.class);

	@Autowired
	private Predicate predicate;
	
	private String workflowStatusUpdateAddress = MessageBusAddress.WORKFLOW_EVENT_UPDATE_ADDRESS.toString();
	

	public List<RouteDefinition> intercept(CamelContext camelContext, List<RouteDefinition> routeDefinitions) {
		routeDefinitions.forEach(x -> {
			try {
				x.adviceWith(camelContext, new AdviceWithRouteBuilder() {

					@Override
					public void configure() throws Exception {

						interceptFrom("*").when(predicate).to(workflowStatusUpdateAddress).stop();
					}
				});

			} catch (Exception e) {
				LOGGER.error(LoggerFileConstant.SESSIONID.toString(), LoggerFileConstant.USERID.toString(), "",
						"RouteIntercepter::intercept()::exception " + e.getMessage());
			}
		});
		return routeDefinitions;
	}
}