<jsp:directive.include file="/templates/includes.jsp" />
<jsp:directive.include file="/templates/header.jsp" />

<div id="search_form_frame">
	<table id="search_form_table">
	  <tr>
		<td><span class="search_form_label"><c:out value="${msgs.course_id_label}"/>:</span></td>
		<td><input id="input_course_id" class="search_form_input" type="text" name="fname"></td>
		<td><span class="search_form_help"><c:out value="${msgs.course_id_help}"/>:</span></td>
	  </tr>
	  <tr>
		<td><span class="search_form_label"><c:out value="${msgs.course_title_label}"/>:</span></td>
		<td><input id="input_course_title" class="search_form_input" type="text" name="fname"></td>
		<td><span class="search_form_help"><c:out value="${msgs.course_title_help}"/>:</span></td>
	  </tr>
	  <tr>
		<td><span class="search_form_label"><c:out value="${msgs.course_teacher_label}"/>:</span></td>
		<td>
			<select  id="input_course_teacher" data-placeholder="<c:out value="${msgs.course_teacher_select_placeholder}" />" class="chosen">
				<option value></option>
			</select></td>
		<td><span class="search_form_help"><c:out value="${msgs.course_teacher_help}"/>:</span></td>
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
		  <th><c:out value="${msgs.course_id_label}"/></th>
		  <th><c:out value="${msgs.course_title_label}"/></th>
		  <th></th>
		  <th></th>
		</tr>
	  </thead>
	  <tbody></tbody>	  
	</table>
</div>

<jsp:directive.include file="/templates/footer.jsp" />
