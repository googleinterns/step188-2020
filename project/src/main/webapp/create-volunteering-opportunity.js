$(document).ready(function() {
  $('#add_row').click(function() {
    $('#skills').append(getInputDivForSkill());
  });
});

function getInputDivForSkill() {
  return `<input type="text" name="skill" placeholder="Enter a skill" class="form-control" ></tr>`;
}