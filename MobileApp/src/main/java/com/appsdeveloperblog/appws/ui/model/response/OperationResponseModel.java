package com.appsdeveloperblog.appws.ui.model.response;

public class OperationResponseModel {
	
	private String operationResult;
	
	private String operationName;
	
	public OperationResponseModel(String operationResult, String operationName) {
		super();
		this.operationResult = operationResult;
		this.operationName = operationName;
	}
	
	public OperationResponseModel() {
		super();
	}

	public String getOperationResult() {
		return operationResult;
	}

	public void setOperationResult(String operationResult) {
		this.operationResult = operationResult;
	}

	public String getOperationName() {
		return operationName;
	}

	public void setOperationName(String operationName) {
		this.operationName = operationName;
	}
	
}
