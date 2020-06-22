package com.appsdeveloperblog.appws.shared;

import com.amazonaws.regions.Regions;
import com.amazonaws.services.simpleemail.AmazonSimpleEmailService;
import com.amazonaws.services.simpleemail.AmazonSimpleEmailServiceClientBuilder;
import com.amazonaws.services.simpleemail.model.Body;
import com.amazonaws.services.simpleemail.model.Content;
import com.amazonaws.services.simpleemail.model.Destination;
import com.amazonaws.services.simpleemail.model.Message;
import com.amazonaws.services.simpleemail.model.SendEmailRequest;
import com.appsdeveloperblog.appws.shared.dto.UserDto;

public class AmazonSES {
	
	final String FROM = "aaronhealy123@gmail.com";
	
	final String SUBJECT = "One last step to complete registration on Mobile app";
	
	final String HTMLBODY = "<h1>Please verify your email address.</h1>"
			+ "<p>Thank you for registering with our mobile app. To be able to complete registration and log in</p>"
			+ "Click the following link:"
			+ "<a href='http://localhost:8080/verification-service/email-verification?token=$tokenValue'>"
			+ "Final step to complete registration"
			+ "</a><br/><br/>"
			+ "Thank You!";
	
	final String TEXTBODY = "Please verify your email address."
			+ "Thank you for registering with our mobile app. To be able to complete registration and log in"
			+ "Open the following link in web browser:"
			+ "http://localhost:8080/verification-service/email-verification?token=$tokenValue"
			+ "Thank You!";
	
	public void verifyEmail(UserDto userDto)
	{
		// add AWS generated values
		System.setProperty("aws.accessKeyId", ""); 
		System.setProperty("aws.secretKey", ""); 
		
		AmazonSimpleEmailService client = AmazonSimpleEmailServiceClientBuilder.standard().withRegion(Regions.EU_WEST_1).build();
		
		String htmlBodyWithToken= HTMLBODY.replace("$tokenValue", userDto.getEmailVerificationToken());
		
		String textBodyWithToken = TEXTBODY.replace("$tokenValue", userDto.getEmailVerificationToken());
		
		SendEmailRequest request = new SendEmailRequest()
				.withDestination(new Destination().withToAddresses(userDto.getEmail()))
				.withMessage(new Message().withBody(new Body().withHtml(new Content().withCharset("UTF-8").withData(htmlBodyWithToken))
						.withText(new Content().withCharset("UTF-8").withData(textBodyWithToken)))
						.withSubject(new Content().withCharset("UTF-8").withData(SUBJECT)))
				.withSource(FROM);
		
		client.sendEmail(request);
		
		System.out.println("Email Sent!");
	}
	
	public boolean sendPasswordResetRequest(String firstname, String email, String token)
	{
		return false;
	}

}
