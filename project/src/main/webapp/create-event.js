$(function() {
  populatePrefilled('interests');
  populatePrefilled('skills');
});

function togglePrefilledSkills() {
  $('#prefilled-skills').toggle();
}

function togglePrefilledInterests() {
  $('#prefilled-interests').toggle();
}
