window.onload = function onLoad() {
  getVolunteeringOpportunities();
};

/**
 * Add the volunteering opportunities to the event card.
 */
async function getVolunteeringOpportunities() {
  const response = await fetch('/event-volunteering-data');
  const opportunities = await response.json()
  for (const key in opportunities) {
    console.log(opportunities[key].name);
    console.log(opportunities[key].numSpotsLeft);
    $('#volunteering-opportunities')
        .append(getInputFieldForOpportunity(
            opportunities[key].name, opportunities[key].numSpotsLeft,
            opportunities[key].requiredSkills));
  }
}

/**
 * Return a string for a list HTML element representing an opportunity.
 * @param name name of opportunity
 * @param numSpotsLeft number of spots left for opportunity
 * @param requiredSkills required skills for opportunity
 * @return {string}
 */
function getInputFieldForOpportunity(name, numSpotsLeft, requiredSkills) {
  requiredSkillsString = requiredSkills.length ? requiredSkills.toString() : "None";
  return `<li class="list-group-item">
          <p class="card-text">Volunteer Name: ${name}</p>
           <p class="card-text">Volunteer Spots Left: ${numSpotsLeft}</p>
           <p class="card-text">Required Skills: ${requiredSkillsString}</p></li>`;
}