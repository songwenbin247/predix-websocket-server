package com.ge.predix.solsvc.websocket.server.endpoint;

import java.io.IOException;
import java.util.LinkedList;
import java.util.List;

import javax.websocket.CloseReason;
import javax.websocket.EndpointConfig;
import javax.websocket.OnClose;
import javax.websocket.OnError;
import javax.websocket.OnMessage;
import javax.websocket.OnOpen;
import javax.websocket.Session;
import javax.websocket.server.PathParam;
import javax.websocket.server.ServerEndpoint;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.ge.predix.entity.timeseries.datapoints.ingestionrequest.Body;
import com.ge.predix.entity.timeseries.datapoints.ingestionrequest.DatapointsIngestion;
import com.ge.predix.solsvc.ext.util.JsonMapper;

/**
 * 
 * @author predix -
 */
@ServerEndpoint(value = "/livestream/{nodeId}")
public class PredixWebSocketServerEndPoint {
	private static Logger logger = LoggerFactory.getLogger(PredixWebSocketServerEndPoint.class);
		
	private static final LinkedList<Session> clients = new LinkedList<Session>();
	
	private String nodeId;
	
	/**
	 * @param nodeId - nodeId for the session
	 * @param session - session object
	 * @param ec -
	 */
	@OnOpen
	public void onOpen(@PathParam(value = "nodeId") String nodeId, final Session session, EndpointConfig ec) {
		this.nodeId = nodeId;
		clients.add(session);
		logger.info("Server: opened... for Node Id : " + nodeId + " : " + session.getId()); //$NON-NLS-1$ //$NON-NLS-2$
	}

	/**
	 * @param nodeId -
	 * @param message -
	 * @param session -
	 */
	@SuppressWarnings("nls")
    @OnMessage
	public void onMessage(String message, Session session) {
		logger.info("nodeId=" + nodeId + " Websocket Message : " + message);
		try {
			if ("messages".equalsIgnoreCase(nodeId)) { //$NON-NLS-1$
				JsonMapper mapper = new JsonMapper();
				mapper.init();
				DatapointsIngestion dps = mapper.fromJson(message, DatapointsIngestion.class);
				List<Body> nodes = dps.getBody(); //$NON-NLS-1$
				for (Session s : clients) {
					if (!"messages".equals(s.getPathParameters().get("nodeId"))) { //$NON-NLS-1$ //$NON-NLS-2$
						String pNodeName = s.getPathParameters().get("nodeId"); //$NON-NLS-1$
						Body node = findJsonObjectByName(nodes, pNodeName);
						if (node != null) {
							logger.info("Sending to : "+s.getPathParameters().get("nodeId"));
							s.getBasicRemote().sendText(mapper.toJson(node));
						}
					}
				}
				
				if (dps.getMessageId() != null) {
					String response = "{\"messageId\": \"" + dps.getMessageId() + "\",\"statusCode\": 202}"; //$NON-NLS-1$ //$NON-NLS-2$ //$NON-NLS-3$
					session.getBasicRemote().sendText(response);
				}else {
					String response = "{\"messageId\": \"No MSGID Found\",\"statusCode\": 500}"; //$NON-NLS-1$ //$NON-NLS-2$ //$NON-NLS-3$
					session.getBasicRemote().sendText(response);
				}
				
			} else {
				session.getBasicRemote().sendText("SUCCESS"); //$NON-NLS-1$
			}
		} catch (Exception ex) {
			logger.error("Exception in onMessage ", ex); //$NON-NLS-1$
			String response = "{\"messageId\": " + System.currentTimeMillis() + ",\"statusCode\": 500}"; //$NON-NLS-1$ //$NON-NLS-2$ //$NON-NLS-3$
			try {
				session.getBasicRemote().sendText(response);
			} catch (IOException e) {
				logger.info("Exception when sending error response",e);
				throw new RuntimeException(e);
			}
			
		}
	}

	@OnMessage
	public void onMessage(byte[] message, Session session) {
		 
	 }
	
	private Body findJsonObjectByName(List<Body> nodes, String pNodeName) {
		for (int i = 0; i < nodes.size(); i++) {
			Body node = nodes.get(i);
			String nodeName = node.getName(); //$NON-NLS-1$
			if (pNodeName.equalsIgnoreCase(nodeName.trim())) {
				return node;
			}
		}
		return null;
	}

	/**
	 * @param session Session object
	 * @param closeReason the reason of close of session
	 */
	@OnClose
	public void onClose(Session session, CloseReason closeReason) {
		clients.remove(session);
		logger.info("Server: Session " + session.getId() + " closed because of " + closeReason.toString()); //$NON-NLS-1$ //$NON-NLS-2$
	}

	/**
	 * @param session current session object
	 * @param t -instance containing error info
	 */
	@OnError
	public void onError(Session session, Throwable t) {
		logger.error("Server: Session " + session.getId() + " closed because of " + t.getMessage()); //$NON-NLS-1$ //$NON-NLS-2$
	}
}
