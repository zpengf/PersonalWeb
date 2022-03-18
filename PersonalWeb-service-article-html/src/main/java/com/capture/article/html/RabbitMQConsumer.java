package com.capture.article.html;

import com.capture.api.config.RabbitMQConfig;
import com.capture.article.html.controller.ArticleHTMLComponent;
import org.springframework.amqp.core.Message;
import org.springframework.amqp.rabbit.annotation.RabbitListener;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

@Component
public class RabbitMQConsumer {

    @Autowired
    private ArticleHTMLComponent articleHTMLComponent;

    @RabbitListener(queues = {RabbitMQConfig.QUEUE_DOWNLOAD_HTML})
    public void watchQueue(String payload, Message message) {
        System.out.println(payload);

        String routingKey = message.getMessageProperties().getReceivedRoutingKey();
        if (routingKey.equalsIgnoreCase("article.publish.download.do")) {
            System.out.println("article.publish.download.do");

        } else if (routingKey.equalsIgnoreCase("article.success.do")) {
            System.out.println("article.success.do");

        } else if (routingKey.equalsIgnoreCase("article.download.do")) {
            // 消息队列发过来的是 articleId + "," + articleMongoId
            String articleId = payload.split(",")[0];
            String articleMongoId = payload.split(",")[1];

            try {
                articleHTMLComponent.download(articleId, articleMongoId);
            } catch (Exception e) {
                e.printStackTrace();
            }

        } else if (routingKey.equalsIgnoreCase("article.html.delete.do")) {

            String articleId = payload;
            try {
                articleHTMLComponent.delete(articleId);
            } catch (Exception e) {
                e.printStackTrace();
            }

        } else {
            System.out.println("不符合的规则：" + routingKey);
        }

    }

}
