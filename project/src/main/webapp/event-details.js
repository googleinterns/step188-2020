window.onload = function onLoad() {
  getVolunteeringOpportunities();
};

async function getVolunteeringOpportunities() {
    const response = await fetch('/event-volunteering-data');
    const opportunities = response.json();
    console.log(opportunities);
    opportunities.forEach(function(opportunity) {
    console.log(opportunity);
        $('#volunteering-opportunities').append(getInputFieldForOpportunity(opportunity.name, opportunity.numSpotsLeft))
    });
}
/**
 * Return input string for skill input field.
 * @return {string}
 */
function getInputFieldForSkill(name, numSpotsLeft) {
  return `<p class="card-text">Volunteer Name: {name}</p>
              <p class="card-text">Volunteer Spots Left: {numSpotsLeft}</p>`;
}