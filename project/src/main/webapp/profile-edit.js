$(document).ready(function() {
  fetch("/profile-update").then(response => response.json()).then((userData) => {
    const userName = userData['name'];
    const userInterests = userData['interests'];
    const userSkills = userData['skills'];

    $('#name').val(userName);
    $('#interests').val(userInterests);
    $('#skills').val(userSkills);
  });
});

// $("#profile-form").submit(function(){
//   const userName = getUserInformation('name');
//   const userInterests = getUserInformation('interests');
//   const userSkills = getUserInformation('skills');

//   fetch("/profile-update", {
//     method: "post",
//     headers: {
//       'Accept': 'application/json',
//       'Content-Type': 'application/json'
//     },

//     body: JSON.stringify({
//       name: userName,
//       interests: userInterests,
//       skills: userSkills
//     })
//   });
// });

function getUserInformation(fieldName) {
  const element = document.getElementById(fieldName);
  if(element.val() === ''){
      const desiredValue = element.attr('placeholder');
  } else {
      const desiredValue = element.val();
  }
  return desiredValue;
}
