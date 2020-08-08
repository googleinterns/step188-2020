let loginState = null;
let currentUser = null;

window.onload = function onLoad() {
  populateOpportunitiesDropdown();
};

/**
 * Adds the volunteering opportunities to the event card.
 */
async function populateOpportunitiesDropdown() {
  const response = await fetch('/event-volunteering-data');
  const opportunities = await response.json();
  for (const key in opportunities) {
    if (opportunities.hasOwnProperty(key)) {
      $('#opportunities-options')
          .append(getOptionForOpportunity(
              opportunities[key].opportunityId,
              opportunities[key].name));
    }
  }
}

/**
 * Returns button with edit link for the opportunity with ID opportunityId.
 * @param {string} opportunityId Opportunity ID of the opportunity edit link to
 *     return.
 * @return {string}
 */
function getOptionForOpportunity(opportunityId, name) {
  return `<option value=${opportunityId}>${name}</option>`;
}
