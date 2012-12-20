<jsp:directive.include file="/templates/includes.jsp" />
<jsp:directive.include file="/templates/header.jsp" />

<div id="search_form_frame">
	<table id="search_form_table">
	  <tr>
		<td><span class="search_form_label">Numero:</span></td>
		<td><input id="input_course_id" class="search_form_input" type="text" name="fname"></td>
	  </tr>
	  <tr>
		<td><span class="search_form_label">Titre:</span></td>
		<td><input id="input_course_title" class="search_form_input" type="text" name="fname"></td>
	  </tr>
	  <tr>
		<td><span class="search_form_label">Enseignant:</span></td>
		<td>
<select  id="input_course_teacher" data-placeholder="S�lectionner un enseignant" class="chosen">
	<option value></option>
	<option>Abraham, Yves-Marie</option>
	<option >Aktouf, Omar</option>
	<option>Allain, �lodie</option>
	<option>Allali, Brahim</option>
	<option>Ananou, Claude</option>
	<option>Arcand, S�bastien</option>
	<option>Aub�, Caroline</option>
	<option>Aubert, Benoit A.</option>
	<option>Babin, Gilbert</option>
	<option>Bahn, Olivier</option>
	<option>Balloffet, Pierre</option>
	<option>Bareil, C�line</option>
	<option>Bar�s, Franck</option>
	<option>Barin Cruz, Luciano</option>
	<option>Barki, Henri</option>
	<option>Barnea, Amir</option>
	<option>Bauwens, Luc</option>
	<option>Beauchamp, Charlotte</option>
	<option>Beaudoin, Claude</option>
	<option>B�chard, Jean-Pierre</option>
	<option>B�dard, Ren�e</option>
	<option>B�langer, Carol</option>
	<option>B�langer-Martin, Luc</option>
	<option>Bellavance, Fran�ois</option>
	<option>Belzile, Germain</option>
	<option>Ben Ameur, Hatem</option>
	<option>Bitektine, Alexandre B.</option>
	<option>Boisvert, Hugues</option>
	<option>Bouakez, Hafedh</option>
</select></td>
	  </tr>
	  <tr>
		<td></td>
		<td><div id="search_form_button_container"><span id="search_form_button">Recherche</span></div></td>
	  </tr>
	</table>
</div>

<div id="search_result_frame">
	<table id="search_result_table">
	  <thead>
		<tr>
		  <th>No. de r&#233;pertoire</th>
		  <th>Titre</th>
		  <th></th>
		  <th></th>
		</tr>
	  </thead>
	  <tbody></tbody>
	</table>
</div>

<jsp:directive.include file="/templates/footer.jsp" />
