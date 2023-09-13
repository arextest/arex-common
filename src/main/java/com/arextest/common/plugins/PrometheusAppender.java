package com.arextest.common.plugins;

import com.arextest.common.metrics.CommonMetrics;
import com.arextest.common.utils.NetworkInterfaceManager;
import org.apache.logging.log4j.Level;
import org.apache.logging.log4j.core.Filter;
import org.apache.logging.log4j.core.Layout;
import org.apache.logging.log4j.core.LogEvent;
import org.apache.logging.log4j.core.appender.AbstractAppender;
import org.apache.logging.log4j.core.config.plugins.Plugin;
import org.apache.logging.log4j.core.config.plugins.PluginAttribute;
import org.apache.logging.log4j.core.config.plugins.PluginElement;
import org.apache.logging.log4j.core.config.plugins.PluginFactory;

import java.io.Serializable;

/**
 * @author b_yu
 * @since 2023/9/13
 */
@Plugin(name = "PrometheusAppender", category = "Core", elementType = "appender", printObject = true)
public class PrometheusAppender extends AbstractAppender {
    private String project;

    protected PrometheusAppender(String name,
            Filter filter,
            Layout<? extends Serializable> layout,
            String project) {
        super(name, filter, layout);
        this.project = project;
    }

    @PluginFactory
    public static PrometheusAppender createAppender(
            @PluginAttribute("name") String name,
            @PluginElement("Filter") Filter filter,
            @PluginElement("Layout") Layout<? extends Serializable> layout,
            @PluginAttribute("project") String project) {
        return new PrometheusAppender(name, filter, layout, project);
    }

    @Override
    public void append(LogEvent event) {
        if (event.getLevel().intLevel() <= Level.ERROR.intLevel()) {
            CommonMetrics.incErrorCount(project, NetworkInterfaceManager.INSTANCE.getLocalHostAddress());
        }
    }
}
