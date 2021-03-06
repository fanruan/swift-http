package com.fr.swift.cloud.query.info.funnel;

import com.fasterxml.jackson.annotation.JsonProperty;
import com.fr.swift.cloud.query.info.bean.element.filter.FilterInfoBean;

import java.util.List;

/**
 * @author yee
 * @date 2019-06-28
 */
public class FunnelVirtualStep {
    @JsonProperty("name")
    private String name;
    @JsonProperty("events")
    private List<String> events;
    @JsonProperty("filter")
    private FilterInfoBean filter;

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public List<String> getEvents() {
        return events;
    }

    public void setEvents(List<String> events) {
        this.events = events;
    }

    public FilterInfoBean getFilter() {
        return filter;
    }

    public void setFilter(FilterInfoBean filter) {
        this.filter = filter;
    }

    @Override
    public String toString() {
        return "FunnelVirtualStep{" +
                "name='" + name + '\'' +
                ", events=" + events +
                '}';
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (!(o instanceof FunnelVirtualStep)) return false;

        FunnelVirtualStep that = (FunnelVirtualStep) o;

        return events != null ? events.equals(that.events) : that.events == null;
    }

    @Override
    public int hashCode() {
        return events != null ? events.hashCode() : 0;
    }
}
