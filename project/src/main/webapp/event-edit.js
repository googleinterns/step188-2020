/**
 * Gets event details from database to put in form and allow edits
 */
async function getEventDetails() {
  const queryString = window.location.search;
  const urlParams = new URLSearchParams(queryString);
  const eventId = urlParams.get('eventId');
  const response = await fetch('/create-event?' + new URLSearchParams({'eventId': eventId}))
  const data = await response.json();
  document.getElementById('name').value = data['name'];
  document.getElementById('description').value = data['description'];
  document.getElementById('date').value =
    `${data['date'].month}/${data['date'].dayOfMonth}/${data['date'].year}`;
  document.getElementById('time').value = data['time'];
  document.getElementById('location').value = data['location'];
  const link = '/event-details.html?eventId=' + eventId;
  document.getElementById('editLink').setAttribute('href', link);
}
