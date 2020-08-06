$(function() {
  populatePrefilledSkills();
  // $.getScript('https://cdnjs.cloudflare.com/ajax/libs/bootstrap-tagsinput/0.8.0/bootstrap-tagsinput.js', function() {
  //   $('#skills').tagsinput();
  // });
});

/** Adds skill tags to input box when corresponding option is clicked */
async function populatePrefilledSkills() {
  $('#prefilled-skills').hide();
  const prefilledSkills = await getPrefilledSkills();
  for (const skill of prefilledSkills) {
    // Build the skill option
    const newTag = buildSkillTag(skill);
    newTag.querySelector('.btn-skill').textContent = skill;
    newTag.querySelector('.adder').addEventListener('click', moveTagFromPoolToInput);
    $('#prefilled-skills').append(newTag);
  }
}

function moveTagFromPoolToInput() {
  $('#skills').tagsinput('add', $(this).next().html());
  // Remove skill option once it's chosen
  $(this).parent().hide();
}

function togglePrefilledSkills() {
  $('#prefilled-skills').toggle();
}
