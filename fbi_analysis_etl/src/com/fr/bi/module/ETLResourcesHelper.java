/**
 * 
 */
package com.fr.bi.module;

/**
 * @author Daniel
 *
 */
public class ETLResourcesHelper {

	public static final String DEFAULT_JS = "analysis_etl.js";
	public static final String DEFAULT_CSS = "analysis_etl.css";

	/**
	 * @return
	 */
	public static String[] getDefaultJs() {
		return new String[] {
				"com/fr/bi/etl/analysis/web/js/base/base.js",
				"com/fr/bi/etl/analysis/web/js/base/req.js",
				"com/fr/bi/etl/analysis/web/js/base/constant/etlconst.js",
				"com/fr/bi/etl/analysis/web/js/base/constant/enums.js",
				"com/fr/bi/etl/analysis/web/js/base/constant/constant.js",
				"com/fr/bi/etl/analysis/web/js/base/plugin.js",
				"com/fr/bi/etl/analysis/web/js/base/utils.js",
				"com/fr/bi/etl/analysis/web/js/base/widget/etl_main.js",
				"com/fr/bi/etl/analysis/web/js/base/widget/etl_main.control.js",
				"com/fr/bi/etl/analysis/web/js/base/widget/etl_main.model.js",
				"com/fr/bi/etl/analysis/web/js/base/widget/button/leftpointer.js",
				"com/fr/bi/etl/analysis/web/js/base/widget/button/drag.button.js",
				"com/fr/bi/etl/analysis/web/js/base/widget/button/toppointer.js",
				"com/fr/bi/etl/analysis/web/js/base/widget/button/leftpointerbutton.js",
				"com/fr/bi/etl/analysis/web/js/base/widget/selectdata/tree/node/node.level8.js",
				"com/fr/bi/etl/analysis/web/js/base/widget/selectdata/tree/node/node.level8.control.js",
				"com/fr/bi/etl/analysis/web/js/base/widget/popover/rename.popover.js",
				"com/fr/bi/etl/analysis/web/js/base/widget/relation/branch.relation.js",
				"com/fr/bi/etl/analysis/web/js/base/widget/popover/rename.popover.control.js",
				"com/fr/bi/etl/analysis/web/js/base/widget/popover/name.popover.js",
				"com/fr/bi/etl/analysis/web/js/base/widget/popover/warning.popover.js",
				"com/fr/bi/etl/analysis/web/js/modules/base/tabs/widget.etldatastyletab.js",
				"com/fr/bi/etl/analysis/web/js/base/widget/tab/view/sheetbutton.js",
				"com/fr/bi/etl/analysis/web/js/base/widget/table/preview.table.js",
				"com/fr/bi/etl/analysis/web/js/base/widget/table/merge/merge.preview.table.js",
				"com/fr/bi/etl/analysis/web/js/base/widget/table/merge/merge.preview.header.cell.js",
				"com/fr/bi/etl/analysis/web/js/base/widget/table/merge/merge.header.button.js",
				"com/fr/bi/etl/analysis/web/js/base/widget/table/previewtable.cell.js",
				"com/fr/bi/etl/analysis/web/js/base/widget/table/previewtable.header.filter.cell.js",
				"com/fr/bi/etl/analysis/web/js/base/widget/table/previewtable.header.normal.cell.js",
				"com/fr/bi/etl/analysis/web/js/base/widget/table/previewtable.header.delete.cell.js",
				"com/fr/bi/etl/analysis/web/js/base/widget/tab/control/dynamictab.button.control.js",
				"com/fr/bi/etl/analysis/web/js/base/widget/tab/view/dynamictab.button.js",
				"com/fr/bi/etl/analysis/web/js/base/widget/tab/view/dynamictab.js",
				"com/fr/bi/etl/analysis/web/js/base/widget/tab/model/dynamictab.model.js",
				"com/fr/bi/etl/analysis/web/js/base/widget/tab/control/dynamictab.control.js",
				"com/fr/bi/etl/analysis/web/js/base/widget/historytab/model/historytab.model.js",
				"com/fr/bi/etl/analysis/web/js/base/widget/historytab/view/history.group.button.js",
				"com/fr/bi/etl/analysis/web/js/base/widget/historytab/view/historybutton.js",
				"com/fr/bi/etl/analysis/web/js/base/widget/historytab/view/historybutton.all.js",
				"com/fr/bi/etl/analysis/web/js/base/widget/historytab/control/historytab.control.js",
				"com/fr/bi/etl/analysis/web/js/base/widget/historytab/view/historytab.js",
				"com/fr/bi/etl/analysis/web/js/base/widget/line/horizontal.line.js",
				"com/fr/bi/etl/analysis/web/js/base/widget/line/vertical.line.js",
				"com/fr/bi/etl/analysis/web/js/base/widget/line/rectangle.js",
				"com/fr/bi/etl/analysis/web/js/base/widget/line/single.line.js",
				"com/fr/bi/etl/analysis/web/js/base/widget/list/sortable.list.js",
				"com/fr/bi/etl/analysis/web/js/base/widget/mergehistory/view/merge.history.js",
				"com/fr/bi/etl/analysis/web/js/base/widget/mergehistory/control/merge.history.control.js",
				"com/fr/bi/etl/analysis/web/js/base/widget/button/title/group/operator.title.js",
				"com/fr/bi/etl/analysis/web/js/base/widget/button/title/group/operator.button.js",
				"com/fr/bi/etl/analysis/web/js/base/widget/operator/selectdata/view/node/node.level0.js",
				"com/fr/bi/etl/analysis/web/js/base/widget/operator/selectdata/view/node/node.level1.date.js",
				"com/fr/bi/etl/analysis/web/js/base/widget/operator/selectdata/view/node/node.level2.date.js",
				"com/fr/bi/etl/analysis/web/js/base/widget/operator/selectdata/view/node/node.level1.js",
				"com/fr/bi/etl/analysis/web/js/base/widget/operator/selectdata/view/treeitem/item.level0.js",
				"com/fr/bi/etl/analysis/web/js/base/widget/operator/selectdata/view/treeitem/item.level1.js",
				"com/fr/bi/etl/analysis/web/js/base/widget/operator/selectdata/view/treeitem/item.level2.js",
				"com/fr/bi/etl/analysis/web/js/base/widget/operator/operator.center.js",
				"com/fr/bi/etl/analysis/web/js/base/widget/operator/operator.abstract.control.js",
				"com/fr/bi/etl/analysis/web/js/base/widget/operator/operator.abstract.js",
				"com/fr/bi/etl/analysis/web/js/base/widget/operator/operator.center.control.js",
				"com/fr/bi/etl/analysis/web/js/base/widget/operator/addcolumn/view/add_column.title.js",
				"com/fr/bi/etl/analysis/web/js/base/widget/operator/addcolumn/model/abstract.allfield.pane.model.js",
				"com/fr/bi/etl/analysis/web/js/base/widget/operator/addcolumn/model/add_column.title.model.js",
				"com/fr/bi/etl/analysis/web/js/base/widget/operator/addcolumn/view/all_columns.pane.js",
				"com/fr/bi/etl/analysis/web/js/base/widget/operator/addcolumn/view/button.column.js",
				"com/fr/bi/etl/analysis/web/js/base/widget/operator/addcolumn/control/add_column.title.control.js",
				"com/fr/bi/etl/analysis/web/js/base/widget/operator/addcolumn/view/operator.add_column.js",
				"com/fr/bi/etl/analysis/web/js/base/widget/operator/addcolumn/view/date/date.diff.pane.js",
				"com/fr/bi/etl/analysis/web/js/base/widget/operator/addcolumn/view/date/date.convert.pane.js",
				"com/fr/bi/etl/analysis/web/js/base/widget/operator/addcolumn/view/date/date.convert.pane.year.js",
				"com/fr/bi/etl/analysis/web/js/base/widget/operator/addcolumn/view/date/date.convert.pane.month.js",
				"com/fr/bi/etl/analysis/web/js/base/widget/operator/addcolumn/view/date/date.convert.pane.season.js",
				"com/fr/bi/etl/analysis/web/js/base/widget/operator/addcolumn/view/expr/expr.acc.pane.js",
				"com/fr/bi/etl/analysis/web/js/base/widget/operator/addcolumn/view/expr/expr.rank.pane.js",
				"com/fr/bi/etl/analysis/web/js/base/widget/operator/addcolumn/view/expr/expr.sum.pane.js",
				"com/fr/bi/etl/analysis/web/js/base/widget/operator/addcolumn/view/expr/expr.period.pane.js",
				"com/fr/bi/etl/analysis/web/js/base/widget/operator/addcolumn/view/expr/expr.last_period.pane.js",
				"com/fr/bi/etl/analysis/web/js/base/widget/operator/addcolumn/view/expr/expr.last_period.percent.pane.js",
				"com/fr/bi/etl/analysis/web/js/base/widget/operator/addcolumn/view/expr/expr.same_period.pane.js",
				"com/fr/bi/etl/analysis/web/js/base/widget/operator/addcolumn/view/expr/expr.same_period.percent.pane.js",
				"com/fr/bi/etl/analysis/web/js/base/widget/operator/addcolumn/view/formula/formula.pane.js",
				"com/fr/bi/etl/analysis/web/js/base/widget/operator/addcolumn/view/value/value.convert.pane.js",
				"com/fr/bi/etl/analysis/web/js/base/widget/operator/addcolumn/view/value/value.group.pane.js",
				"com/fr/bi/etl/analysis/web/js/base/widget/operator/addcolumn/view/value/value.group.single.pane.js",
				"com/fr/bi/etl/analysis/web/js/base/widget/operator/addcolumn/view/value/value.single.pane.js",
				"com/fr/bi/etl/analysis/web/js/base/widget/operator/addcolumn/model/value/value.convert.model.js",
				"com/fr/bi/etl/analysis/web/js/base/widget/operator/addcolumn/model/expr/expr.number_fields.model.js",
				"com/fr/bi/etl/analysis/web/js/base/widget/operator/addcolumn/control/date/date.diff.control.js",
				"com/fr/bi/etl/analysis/web/js/base/widget/operator/addcolumn/control/date/date.convert.control.js",
				"com/fr/bi/etl/analysis/web/js/base/widget/operator/addcolumn/control/expr/expr.acc.control.js",
				"com/fr/bi/etl/analysis/web/js/base/widget/operator/addcolumn/control/expr/expr.rank.control.js",
				"com/fr/bi/etl/analysis/web/js/base/widget/operator/addcolumn/control/expr/expr.sum.control.js",
				"com/fr/bi/etl/analysis/web/js/base/widget/operator/addcolumn/control/expr/expr.period.control.js",
				"com/fr/bi/etl/analysis/web/js/base/widget/operator/addcolumn/control/expr/expr.last_period.control.js",
				"com/fr/bi/etl/analysis/web/js/base/widget/operator/addcolumn/control/expr/expr.last_period.percent.control.js",
				"com/fr/bi/etl/analysis/web/js/base/widget/operator/addcolumn/control/expr/expr.same_period.control.js",
				"com/fr/bi/etl/analysis/web/js/base/widget/operator/addcolumn/control/expr/expr.same_period.percent.control.js",
				"com/fr/bi/etl/analysis/web/js/base/widget/operator/addcolumn/control/formula/formula.pane.control.js",
				"com/fr/bi/etl/analysis/web/js/base/widget/operator/addcolumn/control/value/value.convert.control.js",
				"com/fr/bi/etl/analysis/web/js/base/widget/operator/addcolumn/control/value/value.group.control.js",
				"com/fr/bi/etl/analysis/web/js/base/widget/operator/addcolumn/control/value/value.single.control.js",
				"com/fr/bi/etl/analysis/web/js/base/widget/operator/filter/view/operator.filter.js",
				"com/fr/bi/etl/analysis/web/js/base/widget/operator/filter/view/filter.content.js",
				"com/fr/bi/etl/analysis/web/js/base/widget/operator/filter/view/filter.column.js",
				"com/fr/bi/etl/analysis/web/js/base/widget/operator/filter/view/filter.table.js",
				"com/fr/bi/etl/analysis/web/js/base/widget/operator/group/model/operator.group.pane.model.js",
				"com/fr/bi/etl/analysis/web/js/base/widget/operator/group/view/operator.group.js",
				"com/fr/bi/etl/analysis/web/js/base/widget/operator/group/view/operator.group.pane.js",
				"com/fr/bi/etl/analysis/web/js/base/widget/operator/group/control/operator.group.pane.control.js",
				"com/fr/bi/etl/analysis/web/js/base/widget/operator/addcolumn/model/operator.add_column.pane.model.js",
				"com/fr/bi/etl/analysis/web/js/base/widget/operator/addcolumn/view/operator.add_column.pane.js",
				"com/fr/bi/etl/analysis/web/js/base/widget/operator/addcolumn/control/operator.add_column.pane.control.js",
				"com/fr/bi/etl/analysis/web/js/base/widget/operator/filter/view/operator.filter.pane.js",
				"com/fr/bi/etl/analysis/web/js/base/widget/operator/filter/control/operator.filter.control.js",
				"com/fr/bi/etl/analysis/web/js/base/widget/operator/filter/control/operator.filter.pane.control.js",
//				"com/fr/bi/web/js/modules/onepackage/etl/statistic/widget.statistic.model.js",
//				"com/fr/bi/web/js/modules/etl/partfield/widget.selectpartfieldlist.js",
				"com/fr/bi/etl/analysis/web/js/base/widget/operator/mergesheet/model/merge.fields.model.js",
				"com/fr/bi/etl/analysis/web/js/base/widget/operator/mergesheet/model/merge.model.js",
				"com/fr/bi/etl/analysis/web/js/base/widget/operator/mergesheet/view/merge.js",
				"com/fr/bi/etl/analysis/web/js/base/widget/operator/mergesheet/view/merge.fields.js",
				"com/fr/bi/etl/analysis/web/js/base/widget/operator/mergesheet/view/merge.type.js",
				"com/fr/bi/etl/analysis/web/js/base/widget/operator/mergesheet/view/merge.preview.js",
				"com/fr/bi/etl/analysis/web/js/base/widget/operator/mergesheet/view/operator.merge.js",
				"com/fr/bi/etl/analysis/web/js/base/widget/operator/mergesheet/view/operator.merge.pane.js",
				"com/fr/bi/etl/analysis/web/js/base/widget/operator/mergesheet/view/popover/choosesheet.js",
				"com/fr/bi/etl/analysis/web/js/base/widget/operator/mergesheet/view/popover/choosesheet.button.js",
				"com/fr/bi/etl/analysis/web/js/base/widget/operator/mergesheet/control/popover/choosesheet.control.js",
				"com/fr/bi/etl/analysis/web/js/base/widget/operator/mergesheet/control/merge.control.js",
				"com/fr/bi/etl/analysis/web/js/base/widget/operator/mergesheet/control/merge.fields.control.js",
				"com/fr/bi/etl/analysis/web/js/base/widget/operator/mergesheet/control/merge.preview.control.js",
				"com/fr/bi/etl/analysis/web/js/base/widget/operator/mergesheet/control/merge.type.control.js",
				"com/fr/bi/etl/analysis/web/js/base/widget/operator/mergesheet/control/operator.merge.pane.control.js",
				"com/fr/bi/etl/analysis/web/js/base/widget/operator/usepart/operator.usepart.js",
				"com/fr/bi/etl/analysis/web/js/base/widget/operator/usepart/operator.usepart.pane.js",
				"com/fr/bi/etl/analysis/web/js/base/widget/operator/usepart/operator.usepart.control.js",
				"com/fr/bi/etl/analysis/web/js/base/widget/operator/usepart/operator.usepart.pane.control.js",
				"com/fr/bi/etl/analysis/web/js/base/widget/operator/selectnonedata/select.none.data.js",
				"com/fr/bi/etl/analysis/web/js/base/widget/operator/selectnonedata/select.none.data.pane.js",
				"com/fr/bi/etl/analysis/web/js/base/widget/operator/selectnonedata/select.none.data.control.js",
				"com/fr/bi/etl/analysis/web/js/base/widget/operator/selectdata/view/select.data.js",
				"com/fr/bi/etl/analysis/web/js/base/widget/operator/selectdata/model/select.data.model.js",
				"com/fr/bi/etl/analysis/web/js/base/widget/operator/selectdata/control/select.data.control.js",
				"com/fr/bi/etl/analysis/web/js/base/widget/operator/selectdata/view/widget.selectdatapane.js",
				"com/fr/bi/etl/analysis/web/js/base/widget/operator/selectdata/control/widget.selectdatapane.control.js",
				"com/fr/bi/etl/analysis/web/js/base/widget/pane/toppointerpane.js",
				"com/fr/bi/etl/analysis/web/js/base/widget/pane/toppointersavepane.js",
				"com/fr/bi/etl/analysis/web/js/base/widget/pane/toppointersavepane.control.js",
				"com/fr/bi/etl/analysis/web/js/modules/filter/filter.popup.view.etl.js",
				"com/fr/bi/etl/analysis/web/js/modules/filter/filter.view.item.factory.js",
				"com/fr/bi/etl/analysis/web/js/modules/filter/filter.combo.pane.etl.js",
				"com/fr/bi/etl/analysis/web/js/modules/filter/filter.combo.etl.js",
				"com/fr/bi/etl/analysis/web/js/modules/filter/item/abstract.filter.item.etl.js",
				"com/fr/bi/etl/analysis/web/js/modules/filter/item/filter.item.string.etl.js",
				"com/fr/bi/etl/analysis/web/js/modules/filter/item/filter.item.date.etl.js",
				"com/fr/bi/etl/analysis/web/js/modules/filter/item/filter.item.number.etl.js",
				"com/fr/bi/etl/analysis/web/js/modules/filter/item/filter.value.chooser.etl.js",
				"com/fr/bi/etl/analysis/web/js/modules/filter/item/filter.date.range.etl.js",
				"com/fr/bi/etl/analysis/web/js/modules/etl.config.js",
				"com/fr/bi/etl/analysis/web/js/modules/filter/item/filter.value.multichooser.pane.etl.js",
				"com/fr/bi/etl/analysis/web/js/modules/filter/item/filter.number.oneside.etl.js",
				"com/fr/bi/etl/analysis/web/js/modules/filter/item/filter.group.etl.js",
				"com/fr/bi/etl/analysis/web/js/modules/filter/item/filter.group.popover.js",
				"com/fr/bi/etl/analysis/web/js/modules/filter/item/filter.formula.etl.js",
				"com/fr/bi/etl/analysis/web/js/modules/filter/item/filter.formula.popover.js",
				"com/fr/bi/etl/analysis/web/js/modules/filter/item/filter.numbern.etl.js",
		};
	}


	public static String[] getDefaultCss() {
		return new String[] {
				"com/fr/bi/etl/analysis/web/css/bibase.css",
				"com/fr/bi/etl/analysis/web/css/lib/font.css",
				"com/fr/bi/etl/analysis/web/css/lib/icon.css",
				"com/fr/bi/etl/analysis/web/css/animate.css",
				"com/fr/bi/etl/analysis/web/css/animate.general.css",
				"com/fr/bi/etl/analysis/web/css/base/widget/etl_main.css",
				"com/fr/bi/etl/analysis/web/css/base/widget/button/drag.button.css",
				"com/fr/bi/etl/analysis/web/css/base/widget/button/leftpointer.css",
				"com/fr/bi/etl/analysis/web/css/base/widget/button/toppointer.css",
				"com/fr/bi/etl/analysis/web/css/base/widget/button/leftpointerbutton.css",
				"com/fr/bi/etl/analysis/web/css/base/widget/tab/sheetbutton.css",
				"com/fr/bi/etl/analysis/web/css/base/widget/tab/dynamictab.button.css",
				"com/fr/bi/etl/analysis/web/css/base/widget/tab/dynamictab.css",
				"com/fr/bi/etl/analysis/web/css/base/widget/historytab/historybutton.css",
				"com/fr/bi/etl/analysis/web/css/base/widget/mergehistory/merge.history.css",
				"com/fr/bi/etl/analysis/web/css/base/widget/historytab/historytab.css",
				"com/fr/bi/etl/analysis/web/css/base/widget/button/title/group/operator.title.css",
				"com/fr/bi/etl/analysis/web/css/base/widget/button/title/group/operator.button.css",
				"com/fr/bi/etl/analysis/web/css/base/widget/operator/selectdata/select.data.css",
				"com/fr/bi/etl/analysis/web/css/base/widget/operator/selectdata/tree/node/node.level8.css",
				"com/fr/bi/etl/analysis/web/css/base/widget/operator/group/operator.group.pane.css",
				"com/fr/bi/etl/analysis/web/css/base/widget/operator/filter/operator.filter.pane.css",
				"com/fr/bi/etl/analysis/web/css/base/widget/operator/usepart/operator.usepart.pane.css",
				"com/fr/bi/etl/analysis/web/css/base/widget/operator/operator.center.css",
				"com/fr/bi/etl/analysis/web/css/base/widget/operator/filter/filter.content.css",
				"com/fr/bi/etl/analysis/web/css/base/widget/operator/filter/filter.column.css",
				"com/fr/bi/etl/analysis/web/css/base/widget/operator/addcolumn/add_column.title.css",
				"com/fr/bi/etl/analysis/web/css/base/widget/operator/addcolumn/add_columns.single.css",
				"com/fr/bi/etl/analysis/web/css/base/widget/operator/addcolumn/all_columns.pane.css",
				"com/fr/bi/etl/analysis/web/css/base/widget/operator/addcolumn/operator.add_column.pane.css",
				"com/fr/bi/etl/analysis/web/css/base/widget/operator/addcolumn/button.column.css",
				"com/fr/bi/etl/analysis/web/css/base/widget/operator/mergesheet/operator.merge.pane.css",
				"com/fr/bi/etl/analysis/web/css/base/widget/operator/mergesheet/merge.css",
				"com/fr/bi/etl/analysis/web/css/base/widget/operator/mergesheet/merge.fields.css",
				"com/fr/bi/etl/analysis/web/css/base/widget/operator/mergesheet/merge.type.css",
				"com/fr/bi/etl/analysis/web/css/base/widget/operator/mergesheet/merge.preview.css",
				"com/fr/bi/etl/analysis/web/css/base/widget/operator/mergesheet/popover/choosesheet.button.css",
				"com/fr/bi/etl/analysis/web/css/base/widget/operator/mergesheet/popover/choosesheet.css",
				"com/fr/bi/etl/analysis/web/css/base/widget/operator/selectdata/widget.selectdatapane.css",
				"com/fr/bi/etl/analysis/web/css/base/widget/table/preview.table.css",
				"com/fr/bi/etl/analysis/web/css/base/widget/table/merge/merge.preview.table.css",
				"com/fr/bi/etl/analysis/web/css/base/widget/table/previewtable.cell.css",
				"com/fr/bi/etl/analysis/web/css/base/widget/table/previewtable.header.cell.css",
				"com/fr/bi/etl/analysis/web/css/base/widget/pane/toppointerpane.css",
				"com/fr/bi/etl/analysis/web/css/base/widget/popover/rename.popover.css",
				"com/fr/bi/etl/analysis/web/css/module/filter/number.oneside.css",
				"com/fr/bi/etl/analysis/web/css/module/filter/filter.group.css",
				"com/fr/bi/etl/analysis/web/css/module/filter/filter.formula.css",
				"com/fr/bi/etl/analysis/web/css/module/filter/filter.css",
		};
	}

}