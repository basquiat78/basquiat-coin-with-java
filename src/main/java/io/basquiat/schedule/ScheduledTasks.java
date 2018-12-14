package io.basquiat.schedule;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;

import io.basquiat.websocket.RequestService;
import io.basquiat.websocket.type.MessageType;

/**
 * 주기적으로 peer에 transaction pool을 요청한다.
 * created by basquiat
 */
@Component
public class ScheduledTasks {

	private static final Logger LOG = LoggerFactory.getLogger(ScheduledTasks.class);

	@Autowired
	private RequestService requestService;

	/**
	 * 15초마다 peer에 요청 메세지 날리기
	 */ 
    @Scheduled(cron = "*/15 * * * * *")
    public void requestTask() {
        LOG.info("call schedule......");
        requestService.request(MessageType.QUERY_TRANSACTIONPOOL);
    }

}
