package com.appsdeveloperblog.appws.shared;

import org.springframework.stereotype.Service;

import com.amazonaws.regions.Regions;
import com.amazonaws.services.simpleemail.AmazonSimpleEmailService;
import com.amazonaws.services.simpleemail.AmazonSimpleEmailServiceClientBuilder;
import com.amazonaws.services.simpleemail.model.Body;
import com.amazonaws.services.simpleemail.model.Content;
import com.amazonaws.services.simpleemail.model.Destination;
import com.amazonaws.services.simpleemail.model.Message;
import com.amazonaws.services.simpleemail.model.SendEmailRequest;
import com.amazonaws.services.simpleemail.model.SendEmailResult;
import com.appsdeveloperblog.appws.shared.dto.UserDto;

@Service
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
	
	final String PASSWORD_RESET_HTMLBODY = "<h1>A request to reset your password</h1>"
		      + "<p>Hi, $firstName!</p> "
		      + "<p>Someone has requested to reset your password with our project. If it were not you, please ignore it."
		      + " otherwise please click on the link below to set a new password: " 
		      + "<a href='http://localhost:8080/verification-service/password-reset.html?token=$tokenValue'>"
		      + " Click this link to Reset Password"
		      + "</a><br/><br/>"
		      + "Thank you!";

		  // The email body for recipients with non-HTML email clients.
		  final String PASSWORD_RESET_TEXTBODY = "A request to reset your password "
		      + "Hi, $firstName! "
		      + "Someone has requested to reset your password with our project. If it were not you, please ignore it."
		      + " otherwise please open the link below in your browser window to set a new password:" 
		      + " http://localhost:8080/verification-service/password-reset.html?token=$tokenValue"
		      + " Thank you!";
	
	public void verifyEmail(UserDto userDto)
	{
		// add AWS generated values
		//System.setProperty("aws.accessKeyId", ""); 
		//System.setProperty("aws.secretKey", ""); 
		
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
		// add AWS generated values
		//System.setProperty("aws.accessKeyId", ""); 
		//System.setProperty("aws.secretKey", "");
		
		boolean returnVal = false;
		
		AmazonSimpleEmailService client = AmazonSimpleEmailServiceClientBuilder.standard().withRegion(Regions.EU_WEST_1).build();
		
		String htmlBodyWithToken = PASSWORD_RESET_HTMLBODY.replace("$tokenValue", token);
		
		htmlBodyWithToken = htmlBodyWithToken.replace("$firstName", firstname);
		
		String textBodyWithToken = PASSWORD_RESET_TEXTBODY.replace("$tokenValue", token);
		
		textBodyWithToken = textBodyWithToken.replace("$firstName", firstname);
		
		SendEmailRequest request = new SendEmailRequest()
				.withDestination(new Destination().withToAddresses(email))
				.withMessage(new Message().withBody(new Body().withHtml(new Content().withCharset("UTF-8").withData(htmlBodyWithToken))
						.withText(new Content().withCharset("UTF-8").withData(textBodyWithToken)))
						.withSubject(new Content().withCharset("UTF-8").withData(SUBJECT)))
				.withSource(FROM);
		
		SendEmailResult result = client.sendEmail(request);
		
		if((result.getMessageId() != null && !result.getMessageId().isEmpty()) && result != null)
		{
			returnVal = true;
		}
		
		return returnVal;
	}

}