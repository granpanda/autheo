package gp.e3.autheo.util;

import java.util.ArrayList;
import java.util.List;

import gp.e3.autheo.authorization.domain.entities.Ticket;

public class TicketFactoryForTests {
	
	public static Ticket getDefaultTestTicket() {
		
		String tokenValue = "tokenValue";
		String httpVerb = "GET";
		String requestedUrl = "www.google.com";
		
		return new Ticket(tokenValue, httpVerb, requestedUrl);
	}
	
	public static Ticket getDefaultTestTicket(int number) {
		
		String tokenValue = "tokenValue" + number;
		String httpVerb = "GET" + number;
		String requestedUrl = "www.google.com" + number;
		
		return new Ticket(tokenValue, httpVerb, requestedUrl);
	}
	
	public static List<Ticket> getTicketList(int listSize) {
		
		List<Ticket> ticketList = new ArrayList<Ticket>();
		
		for (int i = 0; i < listSize; i++) {
			
			ticketList.add(getDefaultTestTicket(i));
		}
		
		return ticketList;
	}
}