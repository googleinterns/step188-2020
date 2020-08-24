$('.grid').masonry({
  itemSelector: '.grid-item',
  columnWidth: '.grid-sizer',
  percentPosition: true,
});

async function setImageFormAction(type) {
  let urlParams = new URLSearchParams({'picture-type': type});
  if (type === 'event') {
    urlParams.append('event-id', getEventId());
  }
  const response = await fetch('/blob-url?' + urlParams);
  const blobUrl = await response.text();
  $('#image-form').attr('action', blobUrl);
}

async function getLoggedInUserEmail() {
  const loginStatus = await getLoginStatus();
  return loginStatus.userEmail;
}

async function isLoggedIn() {
  const loginStatus = await getLoginStatus();
  return loginStatus.loginState === 'LOGGED_IN';
}

async function getLoginStatus() {
  const response = await fetch('/login-status');
  return response.json();
}

/**
 * Get the event id from the query string.
 * @return {string} the event id in the query string
 */
function getEventId() {
  const queryString = window.location.search;
  const urlParams = new URLSearchParams(queryString);
  return urlParams.get('eventId');
}

/**
 * Returns the current login status.
 */
async function getLoginStatus() {
  const response = await fetch('/login-status');
  const loginStatus = await response.json();
  return loginStatus;
}

/** Get label pool */
async function getPrefilledInformation(informationCategory) {
  const response =
      await fetch(
          `/prefilled-information?${new URLSearchParams({'category': informationCategory})}`);
  return response.json();
}

/** Create a label tag with the add button next to it */
function buildTagWithAdder(tagClass, text) {
  let group = buildGroup();
  const addButton = buildAdder(tagClass);
  const tag = buildTag(tagClass);
 
  group.appendChild(addButton);
  group.appendChild(tag);
  group = setText(group, tagClass, text);
  return group;
}

/** Create a label tag without the add button next to it */
function buildTagWithoutAdder(tagClass, text) {
  let group = buildGroup();
  const tag = buildTag(tagClass);

  group.appendChild(tag);
  group = setText(group, tagClass, text);
  return group;
}

/** Create a group for buttons */
function buildGroup() {
  const group = document.createElement('div');
  group.classList.add('btn-group');
  group.setAttribute('role', 'group');
  return group;
}

/** Create an add button */
function buildAdder(tagClass) {
  const addButton = document.createElement('button');
  addButton.setAttribute('type', 'button');
  addButton.classList.add('btn', 'btn-secondary', 'adder', tagClass);
  addButton.textContent = '+';
  return addButton;
}

function setText(group, tagClass, text) {
  group.querySelector('.btn-' + tagClass).textContent = text;
  return group;
}

function buildTag(tagClass) {
  const tag = document.createElement('button');
  tag.setAttribute('type', 'button');
  tag.disabled = true;
  tag.classList.add('btn', 'btn-secondary', 'btn-' + tagClass);
  tag.classList.add(tagClass);
  return tag;
}

/** Adds relevant tags to input box when corresponding option is clicked */
function populatePrefilled(elementId) {
  $('#prefilled-' + elementId).hide();
  getPrefilledInformation(elementId).then((prefilledInfo) => {
    for (const info of prefilledInfo) {
      // Build the option
      const newTag = buildTagWithAdder(elementId, info);
      newTag.querySelector('.adder').addEventListener('click', function() {
        moveTagsFromPoolToInput(this, '#' + elementId);
      });
      $('#prefilled-' + elementId).append(newTag);
    }
  })
}

async function moveTagsFromPoolToInput(clickedAdder, tagId) {
  addTagsToInput(clickedAdder, tagId);
  removeTagsFromPool(clickedAdder);
}

/** Add tag to user input box once it is chosen */
function addTagsToInput(clickedAdder, tagId) {
  $(tagId).on('itemAdded', function() {
    removeExtraInputs(tagId);
  });
  $(tagId).on('itemRemoved', function(event) {
    addTagBackToPool(event.item, tagId);
  });
  getTagsScriptWithCallback(function() {
    $(tagId).tagsinput('refresh');
    $(tagId).tagsinput('add', $(clickedAdder).next().html());
  });
}

/** Removes any extra inputs initialized by scripts */
function removeExtraInputs(className) {
  if ($(className).prevAll().length > 2) {
    const otherName = className === '#interests' ? '#skills' : '#interests';
    $(otherName).prev().remove();
    $(className).prev().prev().remove();
  }
}

function getTagsScriptWithCallback(callback) {
  $.getScript(
      'https://cdnjs.cloudflare.com/ajax/libs/bootstrap-tagsinput/0.8.0/bootstrap-tagsinput.js',
      callback);
}

/** Puts item back into pool if it was a preset */
async function addTagBackToPool(item, tagId) {
  const prefilledItems = await getPrefilledInformation(tagId);
  if (prefilledItems.includes(item)) {
    $(tagId + '-div > .prefilled-pool > .btn-group:contains("' + item + '")')
        .show();
  }
}

/** Remove tag from pool of options once it is chosen */
function removeTagsFromPool(clickedAdder) {
  $(clickedAdder).parent().hide();
}

function togglePrefilledSkills() {
  $('#prefilled-skills').toggle();
}

function togglePrefilledInterests() {
  $('#prefilled-interests').toggle();
}

async function getRankedEvents(events) {
  const response = await fetch('/event-ranker?' + new URLSearchParams({'events': JSON.stringify(events)}));
  return response.json();
}

/** Take ranked events and assign them levels of detail */
function getLodsFromEvents(rankedEvents) {
  const levelOneCutoff = Math.floor(rankedEvents.length / 3);
  const levelTwoCutoff = levelOneCutoff * 2;
  let lodArrayOfMaps = [];
  for (let i = 0; i < rankedEvents.length; i++) {
    const event = rankedEvents[i];
    let lodMap = {'event': event};
    if (i <= levelOneCutoff) {
      lodMap['lod'] = 3;
    } else if (i <= levelTwoCutoff) {
      lodMap['lod'] = 2;
    } else {
      lodMap['lod'] = 1;
    }
    lodArrayOfMaps.push(lodMap);
  }
  return lodArrayOfMaps;
}

/** Writes out relevant details to an event card with the appropriate lod (level of detail) */
async function populateEventContainer(event, containerId, lod) {
  const eventCardAll = await $.get('event-card.html');
  const eventCard = $(eventCardAll).filter(`#event-card-level-${lod}`).get(0);
  const eventCardId = `event-${event.eventId}`;
  $(eventCard).attr('id', eventCardId);
  $(`#${containerId}`).append(eventCard);
  $(`#${eventCardId} #event-card-title`).html(event.name);
  $(`#${eventCardId} #event-card-description`).html(event.description);
  if (lod > 1) {
    $(`#${eventCardId} #event-card-date`)
        .html(
            buildDate(
                event.date.year, event.date.month, event.date.dayOfMonth));
    $(`#${eventCardId} #event-card-time`).html(event.time);
    $(`#${eventCardId} #event-card-location`).html(event.location);
    if (event.opportunities.length) {
      $(`#${eventCardId} #event-card-volunteers`)
        .html(buildVolunteers(event.opportunities));
    } else {
      $(`#${eventCardId} #vols-needed`).parent().hide();
    }
  }
  buildAsLabels(
      `#${eventCardId} .card-body #event-card-labels`, event.labels, 'interests');
  buildSkillsAsLabels(
      `#${eventCardId} .card-body #event-card-labels`, event.opportunities);
  addLinkToRegister(eventCardId);
  addLinkToDetails(eventCardId);
  if (lod >= 2) {
    populateExistingImage('event', `#${eventCardId} #event-card-image`, event.eventId);
  }
}

/** Prepends either a background image or Bootstrap-colored div */
function addEventImage(imageUrl, eventCardId) {
  if (imageUrl) {
    $(`#${eventCardId} #event-card-image`).attr('src', imageUrl);
  }
  else {
    createRandomColorBlock(`#${eventCardId} #event-card-image`);
  }
}

/** Create a fixed-height color block for events with no image */
function createRandomColorBlock(elementId) {
  $(elementId).replaceWith('<div class="card-img-top" id="event-card-image" />')
  $(elementId).addClass(pickRandomColorClass());
  $(elementId).height('200px');
}

/** Pick random color from Bootstrap defaults */
function pickRandomColorClass() {
  const colorClasses =
      ['bg-primary',
       'bg-success',
       'bg-info',
       'bg-warning',
       'bg-danger']
  return colorClasses[Math.floor(Math.random() * colorClasses.length)]
}

/** Adds a hyperlink to the registration button of event card */
function addLinkToRegister(eventCardId) {
  const eventId = eventCardId.substring(6);
  $('#' + eventCardId + ' div #event-register')
      .attr('href', `/event-details.html?eventId=${eventId}&register=true`);
}

/** Adds a hyperlink to the details button of event card */
function addLinkToDetails(eventCardId) {
  const eventId = eventCardId.substring(6);
  $('#' + eventCardId + ' div #event-details')
      .attr('href', `/event-details.html?eventId=${eventId}&register=false`);
}

function buildDate(year, month, dayOfMonth) {
  return month + '/' + dayOfMonth + '/' + year;
}

/** Creates a properly-formatted volunteering opportunity string */
function buildVolunteers(opportunities) {
  let opportunityString = '';
  for (const opportunity of opportunities) {
    opportunityString += opportunity.name + ', ';
  }
  return opportunityString.slice(0, -2);
}

/** Creates a button label for each provided interest or skill */
function buildAsLabels(querySelector, labels, className) {
  for (const label of labels) {
    const newLabelButton = document.createElement('button');
    newLabelButton.classList.add(`btn-${className}`);
    newLabelButton.classList.add('btn');
    newLabelButton.disabled = true;
    newLabelButton.innerHTML = label;
    document
        .querySelector(querySelector)
        .appendChild(newLabelButton);
  }
}

/**
 * Creates the corresponding skill button labels for each volunteering
 * opportunity
 */
function buildSkillsAsLabels(querySelector, opportunities) {
  for (const opportunity of opportunities) {
    buildAsLabels(querySelector, opportunity.requiredSkills, 'skills');
  }
}

/**
 * Adds currently-attributed image
 * If no image, add a default image or background color with default height
 */
async function populateExistingImage(type, elementId, eventId='') {
  if (!eventId) {
    eventId = getEventId();
  }
  const blobKey = await getBlobKey(type, eventId);
  let imageUrl = 'assets/default_profile.jpg';
  const blobResponse = await fetch(`/blob-serve?key=${blobKey}`);
  if (blobResponse.status === 404) {
    createRandomColorBlock(elementId);
  } else {
    const imageBlob = await blobResponse.blob();
    imageUrl = URL.createObjectURL(imageBlob);
    $(elementId).attr('src', imageUrl);
  }
}

async function getBlobKey(type, eventId) {
  let handlerUrl = `/${type}-blob-handler`;
  if (type === 'event') {
    handlerUrl += '?' + new URLSearchParams({'event-id': eventId});
  }
  const handlerResponse =
      await fetch(handlerUrl);
  return handlerResponse.text();
}
