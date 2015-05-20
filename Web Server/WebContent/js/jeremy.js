function toggleDiv(cb, id) {
	if(cb.checked){
		document.getElementById(id).style.display = "block";
	} else {
		document.getElementById(id).style.display = "none";
	}
}

function toggleDivInv(cb, id) {
	if(cb.checked){
		document.getElementById(id).style.display = "none";
	} else {
		document.getElementById(id).style.display = "block";
	}
}