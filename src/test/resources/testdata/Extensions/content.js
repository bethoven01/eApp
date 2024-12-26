(function () {
    // Ensure captured events storage is initialized
    if (!localStorage.getItem('capturedEvents')) {
        localStorage.setItem('capturedEvents', JSON.stringify([]));
    }

    // Utility to fetch all captured events
    function getCapturedEvents() {
        const capturedEvents = JSON.parse(localStorage.getItem('capturedEvents')) || [];
        return capturedEvents.map(event => ({ details: event.details }));
    }

    // Utility to update captured events across tabs/windows
    function updateCapturedEvents(newEvent) {
        const events = JSON.parse(localStorage.getItem('capturedEvents')) || [];
        events.push(newEvent);
        localStorage.setItem('capturedEvents', JSON.stringify(events));
        
        // Notify other tabs/windows of the update
        window.dispatchEvent(new CustomEvent('storageUpdated'));
    }

    // Function to get attributes of an element
    var getAttributes = function (element) {
        var attributes = {};
        if (element.hasAttributes()) {
            var attributeNames = element.getAttributeNames();
            attributeNames.forEach(function (name) {
                attributes[name] = element.getAttribute(name);
            });
        }
        return attributes;
    };

    // Function to generate unique CSS selector for an element
    function getUniqueCssSelector(element) {
        if (!(element instanceof Element)) {
            throw new Error('Provided argument is not an element.');
        }
        var path = [];
        while (element.nodeType === Node.ELEMENT_NODE) {
            var selector = element.nodeName.toLowerCase();
            if (element.id && (element.id.match(/\d+/g) === null)) {
                selector += '#' + element.id;
                path.unshift(selector);
                break;
            } else {
                if (element.className) {
                    var classNames = element.className.trim().replace(/\s+/g, '.');
                    selector += '.' + classNames;
                }
                var sibling = element;
                var nth = 1;
                while ((sibling = sibling.previousElementSibling)) {
                    nth++;
                }
                selector += ':nth-child(' + nth + ')';
                path.unshift(selector);
                if (document.querySelectorAll(path.join(' > ')).length === 1) break;
                element = element.parentNode;
            }
        }
        return path.join(' > ');
    }

    // Function to determine unique locators
    var getUniqueLocators = function (element, id, className, attributes, name, valueAttrib) {
        var ignoreAttributes = ['onclick', 'onload', 'onchange', 'onmouseover', 'onfocus'];
        if (id && (id.match(/\d+/g) === null) && document.getElementById(id) !== null) return ['id', id];
        if (name && document.getElementsByName(name).length == 1) return ['name', name];
        var xpath = '//' + element.localName;
        var xpath1 = '';
        for (var attrName in attributes) {
            if (attributes.hasOwnProperty(attrName) && !ignoreAttributes.includes(attrName)) {
                var attrValue = attributes[attrName].replace(/\"/g, '&quot;');
                xpath1 = xpath + '[@' + attrName + '=\"' + attrValue + '\"]';
                if (document.evaluate(xpath1, document, null, XPathResult.ORDERED_NODE_SNAPSHOT_TYPE, null).snapshotLength === 1) {
                    return ['xpath', xpath1];
                }
            }
        }
        xpath1 = xpath + '[text()=\"' + valueAttrib.replace(/\"/g, '&quot;') + '\"]';
        if (document.evaluate(xpath1, document, null, XPathResult.ORDERED_NODE_SNAPSHOT_TYPE, null).snapshotLength === 1) {
            return ['xpath', xpath1];
        }
		try {
            var cssSelector = getUniqueCssSelector(element);
            if (document.querySelectorAll(cssSelector).length === 1) return ['cssSelector', cssSelector];
        } catch (e) {
            console.error('Css selector is not valid');
        }
        return ['none', ''];
    };

    // Function to log events
    var logEvent = function (eventType, event) {
        if (event.type !== 'load') {
            var attributes = getAttributes(event.target);
            var ariaLabelAttribute = attributes.hasOwnProperty('aria-label') ? attributes['aria-label'] : '';
            var nameAttribute = attributes.hasOwnProperty('name') ? attributes['name'] : '';
            var idAttribute = attributes.hasOwnProperty('id') ? attributes['id'] : '';
            var classAttribute = attributes.hasOwnProperty('class') ? attributes['class'] : '';
            var maxlengthAttribute = attributes.hasOwnProperty('maxlength') ? attributes['maxlength'] : '';
            var placeholderAttribute = attributes.hasOwnProperty('placeholder') ? attributes['placeholder'] : '';
            var value = '';
            var defaultValue = '';
            var isChecked = '';
            var targetElement = event.target;

            if (targetElement.tagName === 'SELECT') {
                var index = targetElement.selectedIndex;
                value = targetElement.options[index].innerText || '';
                defaultValue = targetElement.options[0] ? targetElement.options[0].innerText || '' : '';
            } else if (targetElement.tagName === 'INPUT' && (targetElement.type === 'checkbox' || targetElement.type === 'radio')) {
                isChecked = targetElement.checked ? 'true' : 'false';
                defaultValue = targetElement.defaultChecked ? 'true' : 'false';
                value = isChecked;
            } else {
                value = targetElement.value || '';
                defaultValue = targetElement.innerText || '';
            }

            var uniqueLocator = getUniqueLocators(event.target, idAttribute, classAttribute, attributes, nameAttribute, defaultValue);
            var newEvent = {
                details: {
                    title: document.title,
                    action: event.type,
                    type: targetElement.type || '',
                    target: targetElement.tagName || '',
                    value: value,
                    id: idAttribute,
                    className: classAttribute,
                    localName: targetElement.localName || '',
                    checked: isChecked,
                    attributes: attributes,
                    uniqueLocator: uniqueLocator,
                    ariaLabel: ariaLabelAttribute,
                    defaultValue: defaultValue,
                    defaultChecked: defaultValue,
                    maxlength: maxlengthAttribute,
                    placeholder: placeholderAttribute
                }
            };

            updateCapturedEvents(newEvent);
        }
    };

    // Register event listeners
    ['click', 'dblclick', 'select', 'toggle', 'change', 'hashchange', 'load','input','submit',].forEach(eventType => {
        document.addEventListener(eventType, function (event) {
            logEvent(eventType, event);
        }, true);
    });

    // Listen for updates from other tabs/windows
    window.addEventListener('storage', function (event) {
        if (event.key === 'capturedEvents') {
            console.log('Captured events updated:', JSON.parse(event.newValue));
        }
    });

    // Expose captured events retrieval function
    window.getCapturedEvents = getCapturedEvents;
})();
