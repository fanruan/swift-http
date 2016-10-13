/**
 * 显示标题的详细设置弹出面板
 * Created by AstronautOO7 on 2016/9/28.
 */
BI.ShowTitleDetailedSettingPopup = BI.inherit(BI.Widget, {

    _defaultConfig: function() {
        return BI.extend(BI.ShowTitleDetailedSettingPopup.superclass._defaultConfig.apply(this, arguments), {
            baseCls: "bi-detailed-setting bi-show-title-detailed-setting-popup"
        })
    },

    _init: function() {
        BI.ShowTitleDetailedSettingPopup.superclass._init.apply(this, arguments);
        var self = this;

        //标题栏
        this.titleColour = BI.createWidget({
            type: "bi.global_style_index_background"
        });
        this.titleColour.on(BI.GlobalStyleIndexBackground.EVENT_CHANGE, function () {
            self.fireEvent(BI.ShowTitleDetailedSettingPopup.EVENT_CHANGE);
        });

        var titleColourWrapper = this._createComboWrapper(BI.i18nText("BI-Title_Background"), this.titleColour);

        //标题文字
        this.titleWordStyle = BI.createWidget({
            type: "bi.data_label_text_toolbar",
            cls: "detailed-setting-popup",
            width: 230
        });
        this.titleWordStyle.on(BI.DataLabelTextToolBar.EVENT_CHANGE, function () {
            self.fireEvent(BI.ShowTitleDetailedSettingPopup.EVENT_CHANGE)
        });
        var titleWordStyleWrapper = this._createWrapper(BI.i18nText("BI-Set_Font"), this.titleWordStyle);

        this.centerItems = BI.createWidget({
            type: "bi.vertical",
            element: this.element,
            items: [
                titleColourWrapper,
                titleWordStyleWrapper
            ],
            hgap: 5
        });
    },

    _createComboWrapper: function (name, widget) {
        return {
            type: "bi.left",
            items: [{
                type: "bi.label",
                text: name + ":",
                textAlign: "left",
                height: 30,
                width: 55
            }, widget],
            vgap: 5
        }
    },

    _createWrapper: function (name, widget) {
        return {
            type: "bi.left",
            items: [{
                type: "bi.label",
                text: name + ":",
                textAlign: "left",
                height: 30,
                width: 60
            }, widget],
            vgap: 5
        }
    },

    getValue: function() {
        return {
            detail_background: this.titleColour.getValue(),
            detail_style: this.titleWordStyle.getValue()
        }
    },

    setValue: function(v) {
        v || (v = {});
        this.titleColour.setValue(v.detail_background);
        this.titleWordStyle.setValue(v.detail_style)
    }

});
BI.ShowTitleDetailedSettingPopup.EVENT_CHANGE = "EVENT_CHANGE";
$.shortcut("bi.show_title_detailed_setting_popup", BI.ShowTitleDetailedSettingPopup);