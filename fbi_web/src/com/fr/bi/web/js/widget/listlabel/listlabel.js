/**
 * 文本标签
 *
 * Created by fay on 2016/9/11.
 */
BI.ListLabel = BI.inherit(BI.Widget, {

    _constant: {
        MAX_COLUMN_SIZE: 40,
        DEFAULT_LABEL_GAP: 25,
        DEFAULT_LEFT_GAP: 12,
        LABEL_HEIGHT: 40,
    },

    _defaultConfig: function () {
        return BI.extend(BI.ListLabel.superclass._defaultConfig.apply(this, arguments), {
            baseCls: "bi-list-label",
            title: "",
            showTitle: true
        })
    },

    _init: function () {
        BI.ListLabel.superclass._init.apply(this, arguments);
        var self = this, o = this.options;
        this.items = BI.clone(o.items);
        this.items.unshift({
            text: BI.i18nText("BI-Unlimited"),
            value: "*"
        });
        var title = o.showTitle ? BI.createWidget({
            type: "bi.label",
            text: o.title + " :",
            height: this._constant.LABEL_HEIGHT
        }) : {};
        this.container = BI.createWidget({
            type: "bi.list_label_item_group",
            items: BI.createItems(this.items.slice(0, this._constant.MAX_COLUMN_SIZE), {
                type: "bi.text_button"
            }),
            layouts: [{
                type: "bi.vertical_adapt",
                rgap: this._constant.DEFAULT_LABEL_GAP,
                height: this._constant.LABEL_HEIGHT
            }]
        });
        this.container.on(BI.ButtonGroup.EVENT_CHANGE, function () {
            self.fireEvent(BI.ListLabel.EVENT_CHANGE, arguments);
        });
        this.minTip = BI.createWidget({
            type: "bi.label",
            text: BI.i18nText("BI-No_Selected_Value"),
            disabled: true,
            height: this._constant.LABEL_HEIGHT
        });
        this.maxTip = BI.createWidget({
            type: "bi.label",
            text: BI.i18nText("BI-Max_Show_40_Labels"),
            disabled: true,
            height: this._constant.LABEL_HEIGHT
        });

        this.checkTipsState(o.items);
        this.right = BI.createWidget({
            type: "bi.horizontal",
            items: [this.container, this.minTip, this.maxTip],
            height: this._constant.LABEL_HEIGHT
        });
        BI.createWidget({
            type: "bi.inline",
            items: [title, {
                el: this.right,
                lgap: this._constant.DEFAULT_LEFT_GAP
            }],
            element: this.element
        })
    },

    addItems: function (v) {
        v = v || [];
        this.checkTipsState(v);
        this.container.addItems(v.slice(0, this._constant.MAX_COLUMN_SIZE - 1));
    },

    checkTipsState: function (v) {
        if (BI.isEmptyArray(v)) {
            this.minTip.setVisible(true);
            this.container.setVisible(false);
        } else {
            this.minTip.setVisible(false);
            this.container.setVisible(true);
        }
        if (v.length >= this._constant.MAX_COLUMN_SIZE) {
            this.maxTip.setVisible(true);
        } else {
            this.maxTip.setVisible(false);
        }
    },

    removeAllItems: function () {
        this.container.removeAllItems();
    },

    setValue: function (v) {
        this.container.setValue(v);
    },

    getValue: function () {
        return this.container.getValue();
    }
});

BI.ListLabel.EVENT_CHANGE = 'BI.ListLabel.EVENT_CHANGE';
$.shortcut('bi.list_label', BI.ListLabel);