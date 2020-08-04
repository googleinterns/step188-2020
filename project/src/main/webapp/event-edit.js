/**
 * Gets event details from database to put in form and allow edits
 */
function getEventDetails() {
  const queryString = window.location.search;
  const urlParams = new URLSearchParams(queryString);
  const eventId = urlParams.get('eventId');
  fetch(
      '/create-event?' +
      new URLSearchParams({'eventId': eventId}))
      .then((res) => (res.json())).then((data) => {
        document.getElementById('name').value = data['name'];
        document.getElementById('description').value = data['description'];
        document.getElementById('date').value = `Date: 
          ${data['date'].month}/${data['date'].dayOfMonth}/${data['date'].year}`;
        document.getElementById('location').value =
          `Location: ${data['location']}`;
        
        const link = '/event-details.html?eventId=' + eventId
        document.getElementById('editLink').setAttribute("href", link);
      });
}