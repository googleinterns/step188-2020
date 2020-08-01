// Gets event details from database and fills out event page with details
function getEventDetails() {
  const queryString = window.location.search;
  const urlParams = new URLSearchParams(queryString);
  fetch(
      '/create-event?' +
      new URLSearchParams({'eventId': urlParams.get('eventId')}))
      .then(res => res.json())
      .then(data => {
        document.getElementById('name').innerHTML = data['name'];
        document.getElementById('description').innerHTML = data['description'];
        document.getElementById('date').innerHTML = `Date: ${data['date'].month}/${data['date'].dayOfMonth}/${data['date'].year}`;
        document.getElementById('location').innerHTML  = `Location: ${data['location']}`;
      });
}