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
<select  id="input_course_teacher" data-placeholder="Sélectionner un enseignant" class="chosen">
	<option value></option>
	<option>Abraham, Yves-Marie</option>
	<option >Aktouf, Omar</option>
	<option>Allain, Élodie</option>
	<option>Allali, Brahim</option>
	<option>Ananou, Claude</option>
	<option>Arcand, Sébastien</option>
	<option>Aubé, Caroline</option>
	<option>Aubert, Benoit A.</option>
	<option>Babin, Gilbert</option>
	<option>Bahn, Olivier</option>
	<option>Balloffet, Pierre</option>
	<option>Bareil, Céline</option>
	<option>Barès, Franck</option>
	<option>Barin Cruz, Luciano</option>
	<option>Barki, Henri</option>
	<option>Barnea, Amir</option>
	<option>Bauwens, Luc</option>
	<option>Beauchamp, Charlotte</option>
	<option>Beaudoin, Claude</option>
	<option>Béchard, Jean-Pierre</option>
	<option>Bédard, Renée</option>
	<option>Bélanger, Carol</option>
	<option>Bélanger-Martin, Luc</option>
	<option>Bellavance, François</option>
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
