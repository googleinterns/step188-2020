$(document).ready(function() {
  $('#add_row').click(function() {
    $('#skills').append(getInputFieldForSkill());
  });
});

/**
 * Return input string for skill input field.
 * @return {string}
 */
function getInputFieldForSkill() {
  return `<input type="text" name="skill" placeholder="Enter a skill" \
  class="form-control" ></tr>`;
}
