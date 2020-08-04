window.onload = function onLoad() {
  getVolunteeringOpportunities();
};

/**
 * Add the volunteering opportunities to the event card.
 */
async function getVolunteeringOpportunities() {
  const response = await fetch('/event-volunteering-data');
  const opportunities = await response.json();
  for (const key in opportunities) {
    if (opportunities.hasOwnProperty(key)) {
      $('#volunteering-opportunities')
          .append(getListItemForOpportunity(
              opportunities[key].name, opportunities[key].numSpotsLeft,
              opportunities[key].requiredSkills));
    }
  }
}

/**
 * Return a string for a list HTML element representing an opportunity.
 * @param {string} name name of opportunity
 * @param {string} numSpotsLeft number of spots left for opportunity
 * @param {string[]} requiredSkills skills for opportunity
 * @return {string}
 */
function getListItemForOpportunity(name, numSpotsLeft, requiredSkills) {
  requiredSkillsText =
      requiredSkills.length ? requiredSkills.toString() : 'None';
  return `<li class="list-group-item">
          <p class="card-text">Volunteer Name: ${name}</p>
           <p class="card-text">Volunteer Spots Left: ${numSpotsLeft}</p>
           <p class="card-text">Required Skills: ${
  requiredSkillsText}</p></li>`;
}

