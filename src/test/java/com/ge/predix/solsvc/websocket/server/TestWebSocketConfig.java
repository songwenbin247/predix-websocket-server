package com.ge.predix.solsvc.websocket.server;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

import com.ge.predix.solsvc.websocket.config.DefaultWebSocketConfigForTimeseries;
import com.ge.predix.solsvc.websocket.config.IWebSocketConfig;

/**
 * 
 * @author 212438846
 * Class TestWebSocketConfig1 implements IWebSocketConfig. Every separate websocket server connection requires the
 * IWebSocketConfig interface to be implemented per connection.   
 */
@Component
public class TestWebSocketConfig extends DefaultWebSocketConfigForTimeseries
        implements IWebSocketConfig
{

    /**
     * @param wsUri the wsUri to set
     */
    @Override
    @Value("${predix.websocket.server.uri}")
    public void setWsUri(String wsUri)
    {
        super.setWsUri(wsUri);
    }
    

}
