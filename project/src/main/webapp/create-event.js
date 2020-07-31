// Fetches new event form creation info and stores in Event database
function createCustomEvent() {
  fetch('/create-event').then(response => response.json());
}