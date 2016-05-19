/**
 * 图片组件
 * Created by GameJian on 2016/1/26.
 */
BI.UploadImage = BI.inherit(BI.Widget, {
    _defaultConfig: function () {
        return BI.extend(BI.UploadImage.superclass._defaultConfig.apply(this, arguments), {
            baseCls: "bi-upload-image"
        })
    },

    _init: function () {
        BI.UploadImage.superclass._init.apply(this, arguments);
        var self = this, o = this.options;
        this.label = BI.createWidget({
            type: "bi.text_button",
            trigger: "dblclick",
            cls: "upload-image-text-button-label",
            whiteSpace: "normal",
            text: BI.i18nText("BI-DoubleClick_To_Upload_Image")
        });

        this.file = BI.createWidget({
            type: "bi.multifile_editor",
            accept: "*.jpg;*.png;"
        });

        this.img = BI.createWidget({
            type: "bi.image_button",
            invalid: true
        });

        this.label.on(BI.TextButton.EVENT_CHANGE, function () {
            self.file.select();
        });

        this.file.on(BI.MultifileEditor.EVENT_CHANGE, function (data) {
            var reader = new FileReader();
            reader.onload = function (e) {
                self.img.setSrc(e.target.result);
                self._check();
                self._setSize("auto", "auto");
                self.fireEvent(BI.UploadImage.EVENT_CHANGE, e.target.result);
            };
            reader.readAsDataURL(data.file);
        });

        this.upload = BI.createWidget({
            type: "bi.icon_button",
            cls: "upload-image-icon-button bi-list-item-hover img-upload-font",
            title: BI.i18nText("BI-Upload_Image"),
            height: 32,
            width: 32
        });

        this.upload.on(BI.IconButton.EVENT_CHANGE, function () {
            self.file.select();
        });

        this.del = BI.createWidget({
            type: "bi.icon_button",
            cls: "upload-image-icon-button img-shutdown-font",
            title: BI.i18nText("fbi_Delete"),
            height: 32,
            width: 32
        });

        this.del.on(BI.IconButton.EVENT_CHANGE, function () {
            self.fireEvent(BI.UploadImage.EVENT_DESTROY);
        });

        this.size = BI.createWidget({
            type: "bi.image_button_size_combo",
            cls: "upload-image-icon-button"
        });

        this.size.on(BI.ImageButtonSizeCombo.EVENT_CHANGE, function () {
            self._sizeChange(self.size.getValue());
            self.fireEvent(BI.UploadImage.EVENT_CHANGE, arguments)
        });

        this.href = BI.createWidget({
            type: "bi.image_button_href",
            cls: "upload-image-icon-button"
        });

        this.href.on(BI.ImageButtonHref.EVENT_CHANGE, function () {
            if (BI.isNotEmptyString(self.href.getValue())) {
                self.img.setValid(true)
            } else {
                self.img.setValid(false)
            }
            self.fireEvent(BI.UploadImage.EVENT_CHANGE, arguments)
        });

        this.img.on(BI.ImageButton.EVENT_CHANGE, function () {
            window.open(self.href.getValue());
        });

        BI.createWidget({
            type: "bi.absolute",
            element: this.element,
            items: [{
                el: this.label,
                left: 10,
                right: 10,
                top: 10,
                bottom: 10
            }, {
                el: this.img
            }, {
                el: this.del,
                right: 4,
                top: 4
            }, {
                el: this.href,
                right: 36,
                top: 4
            }, {
                el: this.size,
                right: 68,
                top: 4
            }, {
                el: this.upload,
                right: 100,
                top: 4
            }]
        });

        this.img.invisible();
    },

    _check: function () {
        var f = BI.isNotEmptyString(this.img.getSrc());
        this.label.setVisible(!f);
        this.img.visible(f);
    },

    _setSize: function (w, h) {
        this.img.setWidth(w);
        this.img.setHeight(h)
    },

    _sizeChange: function (size) {
        var self = this, o = this.options;
        switch (size) {
            case BI.ImageButtonSize.ORIGINAL:
                self._setSize("auto", "auto");
                break;
            case BI.ImageButtonSize.EQUAL:
                self._setSize("auto", o.height);
                break;
            case BI.ImageButtonSize.WIDGET_SIZE:
                self._setSize("100%", "100%");
                break;
        }
    },

    getValue: function () {
        return {href: this.href.getValue(), size: this.size.getValue(), src: this.img.getSrc()}
    },

    setValue: function (v) {
        var self = this;
        v || (v = {});
        if (BI.isNotEmptyString(v.href)) {
            self.img.setValid(true)
        }
        this.href.setValue(v.href);
        this.size.setValue(v.size);
        this.img.setSrc(v.src);
        this._check();
        this._sizeChange(v.size)
    }
});

BI.UploadImage.EVENT_DESTROY = "EVENT_DESTROY";
BI.UploadImage.EVENT_CHANGE = "EVENT_CHANGE";
$.shortcut("bi.upload_image", BI.UploadImage);