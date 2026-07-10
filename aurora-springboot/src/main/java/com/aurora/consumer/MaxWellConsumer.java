package com.aurora.consumer;

import com.alibaba.fastjson.JSON;
import com.aurora.entity.Article;
import com.aurora.model.dto.ArticleSearchDTO;
import com.aurora.model.dto.MaxwellDataDTO;
import com.aurora.repository.ElasticsearchMapper;
import com.aurora.util.BeanCopyUtil;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.amqp.rabbit.annotation.RabbitHandler;
import org.springframework.amqp.rabbit.annotation.RabbitListener;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import static com.aurora.constant.RabbitMQConstant.MAXWELL_QUEUE;

@Component
@RabbitListener(queues = MAXWELL_QUEUE)
public class MaxWellConsumer {

    private static final Logger log = LoggerFactory.getLogger(MaxWellConsumer.class);

    /** Maxwell CDC 操作类型：插入 */
    private static final String TYPE_INSERT = "insert";
    /** Maxwell CDC 操作类型：更新 */
    private static final String TYPE_UPDATE = "update";
    /** Maxwell CDC 操作类型：删除 */
    private static final String TYPE_DELETE = "delete";

    @Autowired
    private ElasticsearchMapper elasticsearchMapper;

    @RabbitHandler
    public void process(byte[] data) {
        if (data == null || data.length == 0) {
            return;
        }
        MaxwellDataDTO maxwellDataDTO = JSON.parseObject(new String(data), MaxwellDataDTO.class);
        if (maxwellDataDTO == null || maxwellDataDTO.getType() == null) {
            return;
        }
        Article article = JSON.parseObject(JSON.toJSONString(maxwellDataDTO.getData()), Article.class);
        if (article == null) {
            return;
        }
        // 根据 CDC 操作类型同步到 ES
        switch (maxwellDataDTO.getType()) {
            case TYPE_INSERT:
            case TYPE_UPDATE:
                elasticsearchMapper.save(BeanCopyUtil.copyObject(article, ArticleSearchDTO.class));
                break;
            case TYPE_DELETE:
                elasticsearchMapper.deleteById(article.getId());
                break;
            default:
                break;
        }
    }
}
