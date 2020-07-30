let lastSkillInputIndex = 0;

$(document).ready(function() {
  $('#add_row').click(function() {
    lastSkillInputIndex++;
    $('#skills').append(getInputDivForSkill(lastSkillInputIndex));
  });
});

function getInputDivForSkill(skillInputIndex) {
  return `<input type="text" name="skill${
      skillInputIndex}"  placeholder="Enter a skill" class="form-control" ></tr>`;
}