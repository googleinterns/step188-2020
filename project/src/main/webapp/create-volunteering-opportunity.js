let numberOfSkills = 1;
$(document).ready(function(){
     $("#add_row").click(function(){
        let lastSkillIndex=numberOfSkills-1;
      $('#skill'+numberOfSkills).html($('#skill'+lastSkillIndex).html()).find('td:first-child').html(numberOfSkills+1);
      $('#skills').append('<input type="text" name="skill'+(numberOfSkills)+'"  placeholder="Enter a skill" class="form-control" ></tr>');
      numberOfSkills++; 
  });
});