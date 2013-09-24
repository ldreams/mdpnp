package org.mdpnp.clinicalscenarios.client.user;

import java.text.SimpleDateFormat;
import java.util.Collections;
import java.util.List;

import org.mdpnp.clinicalscenarios.client.user.comparator.UserComparator;

import com.google.gwt.core.client.GWT;
import com.google.gwt.event.dom.client.ClickEvent;
import com.google.gwt.event.dom.client.ClickHandler;
import com.google.gwt.i18n.client.DefaultDateTimeFormatInfo;
import com.google.gwt.i18n.shared.DateTimeFormat;
import com.google.gwt.uibinder.client.UiBinder;
import com.google.gwt.uibinder.client.UiField;
import com.google.gwt.user.client.Window;
import com.google.gwt.user.client.ui.Composite;
import com.google.gwt.user.client.ui.FlexTable;
import com.google.gwt.user.client.ui.Label;
import com.google.gwt.user.client.ui.Widget;
import com.google.web.bindery.requestfactory.shared.Receiver;
import com.google.web.bindery.requestfactory.shared.ServerFailure;
//import com.ibm.icu.text.SimpleDateFormat;

public class UserInfoSearchPanel extends Composite {
	
	private  static final int EMAIL_COL = 0;
	private static final int TITLE_COL = 1;
	private static final int GIVEN_NAME_COL = 2;
	private static final int FAMILY_NAME_COL = 3;
	private static final int COMPANY_COL = 4;
	private static final int JOB_TITLE_COL = 5;
	private static final int YEARS_IN_FIELD_COL = 6;
	private static final int PHONE_NUMBER_COL = 7;
	private static final int ADMIN_STATUS_COL = 10;//currently not used
	private static final int CREATION_DATE_COL = 8;
	private static final int LATEST_LOGIN_COL = 9;
	
	private static final String STYLE_TABLEROWOTHER = "tableRowOther";
//	private SimpleDateFormat sdf = new SimpleDateFormat("MM/DD/yyyy");
	private com.google.gwt.i18n.shared.DateTimeFormat dateFormatter = new DateTimeFormat("MM/dd/yyyy hh:mm", new DefaultDateTimeFormatInfo()){};

	
//	private boolean reverseOrder;
	private UserComparator comparator = new UserComparator("email", false);
	private UserInfoRequestFactory userInfoRequestFactory;

	private static UserInfoSearchPanelUiBinder uiBinder = GWT.create(UserInfoSearchPanelUiBinder.class);

	interface UserInfoSearchPanelUiBinder extends
			UiBinder<Widget, UserInfoSearchPanel> {
	}


	@UiField
	FlexTable list;
	
	@UiField
	Label userLabel;
	
	private static final String[] headers = new String[] {
		"Email", "Salutation", "First Name", "Last Name", "Organization",
		"Job Title", "Years In Field", "Phone Number", /*"Admin Status",*/ "Created", "Last Loggin"};

	
	
	public UserInfoSearchPanel(UserInfoRequestFactory userInfoRequestFactory) {
		initWidget(uiBinder.createAndBindUi(this));		
		this.userInfoRequestFactory = userInfoRequestFactory;// userInfoRequestFactory.userInfoRequest();
	
		fetchUsersList();
//		uir.findAllUserInfo().to(new Receiver<List<UserInfoProxy>>() {
//			
//			@Override
//			public void onSuccess(final List<UserInfoProxy> response) {
//				drawUsersList(response);			
//			}
//			
//			@Override
//			public void onFailure(ServerFailure error) {
//				super.onFailure(error);
//				Window.alert(error.getMessage());
//			}
//		}).fire();
	}
	
	public void fetchUsersList(){
		UserInfoRequest uir =  userInfoRequestFactory.userInfoRequest();
		uir.findAllUserInfo()
		.to(new Receiver<List<UserInfoProxy>>() {
				
				public void onSuccess(final List<UserInfoProxy> response) {
					drawUsersList(response);			
				}
				
				public void onFailure(ServerFailure error) {
					super.onFailure(error);
					Window.alert(error.getMessage());
					//XXX Log message
				}
			}).fire();		
	}
	
	/**
	 * Draws the list of users
	 * @param response
	 */
	private void drawUsersList(final List<UserInfoProxy> response){		
		list.removeAllRows();
		//HEADERS
		list.insertRow(0);
		list.getRowFormatter().addStyleName(0, "userListHeader"); //TODO Style this table					
		for(int j = 0; j < headers.length; j++) {
			final int col = j;
			Label propLabel = new Label(headers[j]);
			propLabel.setStyleName("clickable");//TODO "Clickable" to a constant?			
			list.setWidget(0, j, propLabel);
			propLabel.addClickHandler(new ClickHandler() {
				
				@Override
				public void onClick(ClickEvent event) {
					//UserComparator comparator = new UserComparator("", reverseOrder);
					setUserComparatorProperty(headers[col]);
					Collections.sort(response, comparator);
					comparator.switcReverseOrder();
//					reverseOrder = !reverseOrder;
					drawUsersList(response);
					
				}
			});
			
		}
		
		//DATA ROWS
		for(int i = 0; i < response.size(); i++) {
			int row = i+1;//add +1 for header row
			list.insertRow(row);
			UserInfoProxy u = response.get(i);
			list.setText(row, EMAIL_COL, u.getEmail());
			list.setText(row, TITLE_COL, u.getTitle());
			list.setText(row, GIVEN_NAME_COL, u.getGivenName());
			list.setText(row, FAMILY_NAME_COL, u.getFamilyName());
			list.setText(row, COMPANY_COL, u.getCompany());
			list.setText(row, JOB_TITLE_COL, u.getJobTitle());
			list.setText(row, YEARS_IN_FIELD_COL, u.getYearsInField());
			list.setText(row, PHONE_NUMBER_COL, u.getPhoneNumber());
//			list.setText(row, ADMIN_STATUS_COL, String.valueOf(u.getAdmin()));					
			list.setText(row, CREATION_DATE_COL, dateFormatter.format(u.getCreationDate()));
			list.setText(row, LATEST_LOGIN_COL, dateFormatter.format(u.getLastLoginDate()));
			if(row%2==0)
				list.getRowFormatter().addStyleName(row, STYLE_TABLEROWOTHER);
			else
				list.getRowFormatter().removeStyleName(row, STYLE_TABLEROWOTHER);
		}
	}
	
	/**
	 * Gets a table column header and returns the according property in the proxy
	 * @return
	 */
	private String getUserInfoProperty(String prop){
		if(prop.equalsIgnoreCase(headers[EMAIL_COL])) return "email";
		if(prop.equalsIgnoreCase(headers[TITLE_COL])) return "title";
		if(prop.equalsIgnoreCase(headers[GIVEN_NAME_COL])) return "givenName";
		if(prop.equalsIgnoreCase(headers[FAMILY_NAME_COL])) return "familyName";
		if(prop.equalsIgnoreCase(headers[COMPANY_COL])) return "company";
		if(prop.equalsIgnoreCase(headers[JOB_TITLE_COL])) return "jobTitle";
		if(prop.equalsIgnoreCase(headers[YEARS_IN_FIELD_COL])) return "yearsInField";
		if(prop.equalsIgnoreCase(headers[PHONE_NUMBER_COL])) return "phoneNumber";
		if(prop.equalsIgnoreCase(headers[ADMIN_STATUS_COL])) return "admin";
		if(prop.equalsIgnoreCase(headers[CREATION_DATE_COL])) return "creationDate";
		if(prop.equalsIgnoreCase(headers[LATEST_LOGIN_COL])) return "lastLoginDate";
		
		return "email";
	}
	
	private void setUserComparatorProperty(String prop){
		if(prop.equalsIgnoreCase(headers[EMAIL_COL])) comparator.setProperty("email");
		else if(prop.equalsIgnoreCase(headers[TITLE_COL])) comparator.setProperty("title");
		else if(prop.equalsIgnoreCase(headers[GIVEN_NAME_COL])) comparator.setProperty("givenName");
		else if(prop.equalsIgnoreCase(headers[FAMILY_NAME_COL])) comparator.setProperty("familyName");
		else if(prop.equalsIgnoreCase(headers[COMPANY_COL])) comparator.setProperty("company");
		else if(prop.equalsIgnoreCase(headers[JOB_TITLE_COL])) comparator.setProperty("jobTitle");
		else if(prop.equalsIgnoreCase(headers[YEARS_IN_FIELD_COL])) comparator.setProperty("yearsInField");
		else if(prop.equalsIgnoreCase(headers[PHONE_NUMBER_COL])) comparator.setProperty("phoneNumber");
		else if(prop.equalsIgnoreCase(headers[ADMIN_STATUS_COL])) comparator.setProperty("admin");
		else if(prop.equalsIgnoreCase(headers[CREATION_DATE_COL])) comparator.setProperty("creationDate");
		else if(prop.equalsIgnoreCase(headers[LATEST_LOGIN_COL])) comparator.setProperty("lastLoginDate");
		else  comparator.setProperty("email");//DEFAULT
	}

}
