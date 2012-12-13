var newUnitTypeTemp = null;
	
function makeReload() {
	document.forms["AdminUnitForm"].submit();
}
	
function changeDocData(selectBox) {
	newUnitTypeTemp = selectBox.value;		
}
	
function chooseNewUnitType() {
	var dialog_buttons = {};
	dialog_buttons['OK'] = function() {
		$('#forSending').attr('value',newUnitTypeTemp);
		$(this).dialog('close');
		makeReload();		
	}; 
	dialog_buttons['Loobu'] = function() {
		$(this).dialog('close');
	};
		
	$('#forUnitTypeChoosing').dialog({ 
		buttons: dialog_buttons,
		closeOnEscape: false,
		modal: true,
		open: function() {
			$('#selectbox option').each(function() {
				if($(this).attr('value') == $('#forSending').attr('value')) {
					$(this).attr('selected','true');
				}
			});
		}
	});
}