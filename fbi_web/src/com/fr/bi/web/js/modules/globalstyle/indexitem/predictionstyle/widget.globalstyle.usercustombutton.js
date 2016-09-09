/**
 * Created by zcf on 2016/9/5.
 */
BI.GlobalStyleUserCustomButton = BI.inherit(BI.BasicButton, {
    _defaultConfig: function () {
        return BI.extend(BI.GlobalStyleUserCustomButton.superclass._defaultConfig.apply(this, arguments), {
            baseCls: "bi-global-style-user-custom-button",
            text: "",
            selected: false,
            value: null
        })
    },

    _init: function () {
        BI.GlobalStyleUserCustomButton.superclass._init.apply(this, arguments);
        var o = this.options;
        var self = this;
        this.button = BI.createWidget({
            type: "bi.global_style_style_button",
            title: o.text,
            value: o.value
        });
        this.deleteButton = BI.createWidget({
            type: "bi.icon_button",
            cls: "close-red-font"
        });
        this.button.on(BI.GlobalStyleStyleButton.EVENT_CHANGE, function () {
            self.fireEvent(BI.GlobalStyleUserCustomButton.EVENT_SELECT)
        });
        this.deleteButton.on(BI.IconButton.EVENT_CHANGE, function () {
            self.fireEvent(BI.GlobalStyleUserCustomButton.EVENT_DELETE)
        });
        this.deleteLayout = BI.createWidget({
            type: "bi.center_adapt",
            items: [this.deleteButton],
            height: 15,
            width: 15
        });
        this.widget = BI.createWidget({
            type: "bi.absolute",
            element: this.element,
            items: [{
                el: this.button
            }, {
                el: this.deleteLayout
            }],
            height: 70,
            width: 110,
            rgap: 0,
            tgap: 0
        });
        this.deleteButton.setVisible(false);
        this.deleteLayout.element.hover(function () {
            self.deleteButton.setVisible(true);
        }, function () {
            self.deleteButton.setVisible(false);
        })
    },

    getValue: function () {
        return this.button.getValue()
    },

    setValue: function (v) {
        this.button.setValue(v);
    }
});
BI.GlobalStyleUserCustomButton.EVENT_SELECT = "BI.GlobalStyleUserCustomButton.EVENT_SELECT";
BI.GlobalStyleUserCustomButton.EVENT_DELETE = "BI.GlobalStyleUserCustomButton.EVENT_DELETE";
$.shortcut("bi.global_style_user_custom_button", BI.GlobalStyleUserCustomButton);