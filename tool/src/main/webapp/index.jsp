<jsp:directive.include file="/templates/includes.jsp" />
<jsp:directive.include file="/templates/header.jsp" />

<div id="search_form_frame">
	<table id="search_form_table">
	  <tr>
		<td><span class="search_form_label">Numero:</span></td>
		<td><input class="search_form_input" type="text" name="fname"></td>
	  </tr>
	  <tr>
		<td><span class="search_form_label">Titre:</span></td>
		<td><input class="search_form_input" type="text" name="fname"></td>
	  </tr>
	  <tr>
		<td><span class="search_form_label">Enseignant:</span></td>
		<td><input class="search_form_input" type="text" name="fname"></td>
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
