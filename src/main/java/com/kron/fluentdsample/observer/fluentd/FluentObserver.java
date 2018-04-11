package com.kron.fluentdsample.observer.fluentd;

import com.kron.fluentdsample.observer.Observer;
import com.kron.fluentdsample.reporter.Reporter;
import com.kron.fluentdsample.entity.TagData;
import org.fluentd.logger.FluentLogger;

import java.text.SimpleDateFormat;
import java.util.Date;

public class FluentObserver implements Observer<TagData> {
    private FluentLogger logger = FluentLogger.getLogger("tag", "fluentd", 24224);

    @Override
    public void update(Reporter<TagData> reporter) {
        TagData tagData = reporter.getValue();
        SimpleDateFormat format = new SimpleDateFormat("yyyyMMddHHmmssSSS");
        String timeTag = format.format(new Date());

        String tag = "report" + timeTag + String.join("", tagData.getId().split(".")) + tagData.getPort();
        logger.log(tag, tagData.toMap());
    }
}
