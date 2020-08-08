let loginState = null;
let currentUser = null;

window.onload = function onLoad() {
  populateOpportunitiesDropdown();
};

/**
 * Adds the volunteering opportunities to the dropdown selection.
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
 * Returns option with given name as text and opportunityId as value.
 * @param {string} opportunityId Opportunity ID of the opportunity option to
 *     return.
 * @return {string}
 */
function getOptionForOpportunity(opportunityId, name) {
  return `<option value=${opportunityId}>${name}</option>`;
}
