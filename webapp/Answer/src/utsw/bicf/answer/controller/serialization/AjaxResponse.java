package utsw.bicf.answer.controller.serialization;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;

/**
 * Helper class to return an ajax response
 * and allow errors or successes not due to login or xss issue
 * @author Guillaume
 *
 */
public class AjaxResponse {
	
	Boolean isAllowed = true;
	boolean success;
	String message;
	Boolean uiProceed = false;
	Boolean skipSnackBar = false;

	public Boolean getIsAllowed() {
		return isAllowed;
	}


	public void setIsAllowed(Boolean isAllowed) {
		this.isAllowed = isAllowed;
	}


	public boolean getSuccess() {
		return success;
	}


	public void setSuccess(boolean success) {
		this.success = success;
	}


	public String getMessage() {
		return message;
	}


	public void setMessage(String message) {
		this.message = message;
	}
	
	
	public String createObjectJSON() throws JsonProcessingException {
		ObjectMapper mapper = new ObjectMapper();
		return mapper.writeValueAsString(this);
	}


	public Boolean getUiProceed() {
		return uiProceed;
	}


	public void setUiProceed(Boolean uiProceed) {
		this.uiProceed = uiProceed;
	}


	public Boolean getSkipSnackBar() {
		return skipSnackBar;
	}


	public void setSkipSnackBar(Boolean skipSnackBar) {
		this.skipSnackBar = skipSnackBar;
	}


	
}
