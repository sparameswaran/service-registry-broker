package org.cf.serviceregistrybroker.controller.broker;

import javax.validation.Valid;

import org.apache.log4j.Logger;
import org.cf.serviceregistrybroker.broker.service.ServiceInstanceBindingService;
import org.cf.serviceregistrybroker.broker.service.ServiceInstanceService;
import org.cf.serviceregistrybroker.exception.ServiceBrokerException;
import org.cf.serviceregistrybroker.exception.ServiceInstanceBindingExistsException;
import org.cf.serviceregistrybroker.exception.ServiceInstanceDoesNotExistException;
import org.cf.serviceregistrybroker.model.ErrorMessage;
import org.cf.serviceregistrybroker.model.ServiceInstance;
import org.cf.serviceregistrybroker.model.ServiceInstanceBinding;
import org.cf.serviceregistrybroker.model.ServiceInstanceBindingResponse;
import org.cf.serviceregistrybroker.model.dto.CreateServiceInstanceBindingRequest;
import org.cf.serviceregistrybroker.model.dto.DeleteServiceInstanceBindingRequest;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;



/**
 * See: Source: http://docs.cloudfoundry.com/docs/running/architecture/services/writing-service.html
 * from: https://github.com/cloudfoundry-community/spring-boot-cf-service-broker/blob/master/src/main/java/org/cloudfoundry/community/servicebroker/controller/ServiceInstanceBindingController.java
 * @author sgreenberg@gopivotal.com
 */
@Controller
public class ServiceInstanceBindingController extends BaseController {

	public static final String BASE_PATH = "/v2/service_instances/{instanceId}/service_bindings";
	
	private static final Logger log = Logger.getLogger(ServiceInstanceBindingController.class);
	
	@Autowired
	ServiceInstanceService serviceInstanceService;
	
	@Autowired
	ServiceInstanceBindingService serviceInstanceBindingService;

	@RequestMapping(value = BASE_PATH + "/{bindingId}", method = RequestMethod.PUT)
	public ResponseEntity<ServiceInstanceBindingResponse> bindServiceInstance(
			@PathVariable("instanceId") String instanceId, 
			@PathVariable("bindingId") String bindingId,
			@Valid @RequestBody CreateServiceInstanceBindingRequest request) throws
			ServiceInstanceDoesNotExistException, ServiceInstanceBindingExistsException, 
			ServiceBrokerException {
		log.debug( "PUT: " + BASE_PATH + "/{bindingId}"
				+ ", bindServiceInstance(), serviceInstance.id = " + instanceId 
				+ ", bindingId = " + bindingId);
		ServiceInstance instance = serviceInstanceService.getServiceInstance(instanceId);
		if (instance == null) {
			throw new ServiceInstanceDoesNotExistException(instanceId);
		}
		ServiceInstanceBinding binding = serviceInstanceBindingService.createServiceInstanceBinding(
				request.withServiceInstanceId(instanceId).and().withBindingId(bindingId));
		log.debug("ServiceInstanceBinding Created: " + binding.getId());
		
        return new ResponseEntity<>(
				new ServiceInstanceBindingResponse(binding),
				HttpStatus.OK);
	}
	
	@RequestMapping(value = BASE_PATH + "/{bindingId}", method = RequestMethod.DELETE)
	public ResponseEntity<String> deleteServiceInstanceBinding(
			@PathVariable("instanceId") String instanceId, 
			@PathVariable("bindingId") String bindingId,
			@RequestParam("service_id") String serviceId,
			@RequestParam("plan_id") String planId) throws ServiceBrokerException, ServiceInstanceDoesNotExistException {
		log.debug( "DELETE: " + BASE_PATH + "/{bindingId}"
				+ ", deleteServiceInstanceBinding(),  serviceInstance.id = " + instanceId 
				+ ", bindingId = " + bindingId 
				+ ", serviceId = " + serviceId
				+ ", planId = " + planId);
        ServiceInstance instance = serviceInstanceService.getServiceInstance(instanceId);
        if (instance == null) {
            throw new ServiceInstanceDoesNotExistException(instanceId);
        }
		ServiceInstanceBinding binding = serviceInstanceBindingService.deleteServiceInstanceBinding(
		        new DeleteServiceInstanceBindingRequest( bindingId, instance, serviceId, planId));
		
		if (binding == null) {
			return new ResponseEntity<>("{}", HttpStatus.GONE);
		}
		log.debug("ServiceInstanceBinding Deleted: " + binding.getId());
        return new ResponseEntity<>("{}", HttpStatus.OK);
	}
	
	@ExceptionHandler(ServiceInstanceDoesNotExistException.class)
	@ResponseBody
	public ResponseEntity<ErrorMessage> handleException(
			ServiceInstanceDoesNotExistException ex) {
	    return getErrorResponse(ex.getMessage(), HttpStatus.UNPROCESSABLE_ENTITY);
	}
	
	@ExceptionHandler(ServiceInstanceBindingExistsException.class)
	@ResponseBody
	public ResponseEntity<ErrorMessage> handleException(
			ServiceInstanceBindingExistsException ex) {
	    return getErrorResponse(ex.getMessage(), HttpStatus.CONFLICT);
	}
	
}
