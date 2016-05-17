/**
 * 图表控件
 * @class BI.BarChart
 * @extends BI.Widget
 */
BI.BarChart = BI.inherit(BI.Widget, {

    _defaultConfig: function () {
        return BI.extend(BI.BarChart.superclass._defaultConfig.apply(this, arguments), {
            baseCls: "bi-bar-chart"
        })
    },

    _init: function () {
        BI.BarChart.superclass._init.apply(this, arguments);
        var self = this, o = this.options;
        this.BarChart = BI.createWidget({
            type: "bi.chart",
            element: this.element
        });
        self.BarChart.setChartType(BICst.WIDGET.BAR);
        this.BarChart.on(BI.Chart.EVENT_CHANGE, function (obj) {
            self.fireEvent(BI.BarChart.EVENT_CHANGE, obj);
        });
    },

    _formatDataForCommon: function (data) {
        var self = this, o = this.options;
        this.regions = [];
        if (BI.has(data, "t")) {
            var top = data.t, left = data.l;
            return BI.map(top.c, function (id, tObj) {
                var data = BI.map(left.c, function (idx, obj) {
                    return {
                        "y": obj.n,
                        "x": obj.s.c[id].s
                    };
                });
                return {
                    name: tObj.n,
                    data: data
                }
            });
        }
        if (BI.has(data, "c")) {
            var obj = (data.c)[0];
            var columnSizeArray = BI.makeArray(BI.isNull(obj) ? 0 : BI.size(obj.s), 0);
            return BI.map(columnSizeArray, function (idx, value) {
                var adjustData = BI.map(data.c, function (id, item) {
                    return {
                        y: item.n,
                        x: item.s[idx]
                    };
                });

                return {
                    data: adjustData
                    //name: o.seriesNames[idx]
                };
            });
        }
        return [];
    },

    _createDataByData: function (da) {
        var self = this, o = this.options;
        var data = this._formatDataForCommon(da);
        if (BI.isEmptyArray(data)) {
            return [];
        }
        return data;
    },

    populate: function (items) {
        this.BarChart.resize();
        this.BarChart.populate(this._createDataByData(items));
    },

    loading: function(){
        this.BarChart.loading();
    },

    loaded: function(){
        this.BarChart.loaded();
    },

    resize: function () {
        this.BarChart.resize();
    }
});
BI.BarChart.EVENT_CHANGE = "EVENT_CHANGE";
$.shortcut('bi.bar_chart', BI.BarChart);