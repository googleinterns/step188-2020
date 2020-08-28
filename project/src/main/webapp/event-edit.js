$(function() {
  getEventDetails();
  populatePrefilled('interests');
  populatePrefilled('skills');
});

/**
 * Gets event details from database to put in form and allow edits
 */
async function getEventDetails() {
  const queryString = window.location.search;
  const urlParams = new URLSearchParams(queryString);
  const eventId = urlParams.get('eventId');
  const response = await fetch('/create-event?' +
    new URLSearchParams({'eventId': eventId}));
  const data = await response.json();
  document.getElementById('name').value = data['name'];
  document.getElementById('description').value = data['description'];
  const date = new Date(`${data['date'].year}/${data['date'].month}/${data['date'].dayOfMonth}`)
  const dateTimeFormat = new Intl.DateTimeFormat('en', 
    { year: 'numeric', month: '2-digit', day: '2-digit' }) 
  const [{ value: month },,{ value: day },,{ value: year }] = dateTimeFormat .formatToParts(date ) 
  document.getElementById('date').value =
    `${year}-${month}-${day}`;
  document.getElementById('time').value = data['time'];
  document.getElementById('location').value = data['location'];
  buildAsLabels(
    '#interests-div .bootstrap-tagsinput', data['labels'], 'interests');
}
