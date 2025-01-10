(function () {
    if (!localStorage.getItem('capturedEvents')) {
        localStorage.setItem('capturedEvents', JSON.stringify([]));
    }

    function getCapturedEvents() {
        const capturedEvents = JSON.parse(localStorage.getItem('capturedEvents')) || [];
        return capturedEvents.map(event => ({ details: event.details }));
    }

    function updateCapturedEvents(newEvent) {
        const events = JSON.parse(localStorage.getItem('capturedEvents')) || [];
        events.push(newEvent);
        localStorage.setItem('capturedEvents', JSON.stringify(events));
        window.dispatchEvent(new CustomEvent('storageUpdated'));
    }

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

    // Helper: Evaluate if a locator is uniquely matching exactly 1 element
    function isUnique(locatorType, locator) {
      try {
        switch (locatorType) {
          case 'id':
            return document.querySelectorAll('#' + locator).length === 1;
          case 'name':
            return document.getElementsByName(locator).length === 1;
          case 'class':
            return document.getElementsByClassName(locator).length === 1;
          case 'tag':
            return document.getElementsByTagName(locator).length === 1;
          case 'linkText':
          case 'partialLinkText':
            const links = Array.from(document.querySelectorAll('a'));
            if (locatorType === 'linkText') {
              return links.filter(a => a.textContent.trim() === locator).length === 1;
            } else {
              return links.filter(a => a.textContent.includes(locator)).length === 1;
            }
          case 'xpath':
            return (
              document.evaluate(
                locator,
                document,
                null,
                XPathResult.ORDERED_NODE_SNAPSHOT_TYPE,
                null
              ).snapshotLength === 1
            );
          case 'cssSelector':
            return document.querySelectorAll(locator).length === 1;
          default:
            return false;
        }
      } catch (err) {
        return false;
      }
    }

    // Helper: Check if an <a> element can be matched by link text or partial link text
    function getLinkTextLocators(element) {
      if (element.localName.toLowerCase() !== 'a') return [];

      const text = (element.textContent || '').trim();
      const locators = [];
      if (text && isUnique('linkText', text)) {
        locators.push(['linkText', text]);
      }

      if (text && text.length > 2 && isUnique('partialLinkText', text)) {
        locators.push(['partialLinkText', text]);
      }
      return locators;
    }

    // Main function to determine unique locators
    function getUniqueLocators(element, id, className, attributes, name, valueAttrib) {
      const ignoreAttributes = [
        'onclick',
        'onload',
        'onchange',
        'onmouseover',
        'onfocus',
        'src',
      ];

      if (
        id &&
        !/\d+/.test(id) && // ensure the ID isn't purely numeric
        isUnique('id', id)
      ) {
        return ['id', id];
      }

      if (name && isUnique('name', name)) {
        return ['name', name];
      }

      if (className && !className.includes('option') && isUnique('class', className)) {
        return ['class', className];
      }

      const localTag = element.localName.toLowerCase();
      if (isUnique('tag', localTag)) {
        return ['tag', localTag];
      }

      const linkTextLocators = getLinkTextLocators(element);
      if (linkTextLocators.length) {
        return linkTextLocators[0];
      }

        if (localTag !== 'svg')
      {
        const xpath = getRelativeXPath(element);
        if (isUnique('xpath', xpath)) {
                return ['xpath', xpath];
        }

      }

      if (element instanceof SVGElement || localTag === 'svg') {
        const tagName = element.tagName.toLowerCase(); // "path", "svg", etc.
        let svgXPath = `//*[name()='${tagName}']`;

        const svgClass = element.getAttribute('class');
        if (svgClass) {
          svgXPath += `[@class='${svgClass}']`;
        }

        const innerText = element.textContent?.trim();
        if (innerText) {
          svgXPath += `[contains(text(),'${innerText}')]`;
        }

        const snapshot = document.evaluate(
          svgXPath,
          document,
          null,
          XPathResult.ORDERED_NODE_SNAPSHOT_TYPE,
          null
        );

        // Find which snapshotItem is our current element
        let elementIndex = 0;
        for (let i = 0; i < snapshot.snapshotLength; i++) {
          if (snapshot.snapshotItem(i) === element) {
            elementIndex = i + 1;
            break;
          }
        }

        // If more than one match, add the index
        if (snapshot.snapshotLength > 1 && elementIndex > 0) {
          svgXPath = `(${svgXPath})[${elementIndex}]`;
        }

        // Check uniqueness
        if (isUnique('xpath', svgXPath)) {
          return ['xpath', svgXPath];
        }
      }

      try {
        // Assume getUniqueCssSelector is implemented somewhere
        const cssSelector = getUniqueCssSelector(element);
        if (isUnique('cssSelector', cssSelector)) {
          return ['cssSelector', cssSelector];
        }
      } catch (e) {
        console.error('CSS selector generation failed:', e);
      }

      /* ---------------------------------------------------------------------------
       * 9) If nothing else is unique, return a no-locator fallback
       * ------------------------------------------------------------------------- */
      return ['none', ''];
    }
 // Function to generate XPath for an element
 function getRelativeXPath(element) {
     if (!(element instanceof Element)) {
         throw new Error('Provided argument is not an element.');
     }

     let tagName = element.tagName.toLowerCase();

     // If the element has an ID, use it directly in the XPath
     if (element.hasAttribute('id')) {
         return `//${tagName}[@id="${element.getAttribute('id')}"]`;
     }

     // Add attributes to make XPath unique, using up to two attributes
     let attributes = getAttributes(element);
     let ignoreAttributes = ['aria-disabled', 'aria-label', 'tabindex', 'aria-selected', 'title', 'viewBox', 'aria-hidden', 'focusable', 'src','style'];

     let usedAttributes = Object.keys(attributes).filter(
         (attr) => !ignoreAttributes.includes(attr)
     );

     let xpath = `//${tagName}`;
     let attributeCount = 0;

     if (usedAttributes.length > 0) {
         usedAttributes.forEach((attr) => {
             if (attributeCount < 2) { // Add up to two attributes
                 xpath += `[@${attr}="${attributes[attr]}"]`;
                 attributeCount++;
             }
         });
     } else {
         // If no attributes are present, use innerText
         let innerText = element.innerText.trim();
         if (innerText) {
             xpath = `//${tagName}[text()="${innerText}"]`;
         } else {
             // Fallback to parent element and indexing if no innerText is present
             let parent = element.parentElement;
             if (parent) {
                 let parentXPath = getRelativeXPath(parent); // Recursive call to get parent's XPath
                 let siblings = Array.from(parent.children).filter(
                     (sibling) => sibling.tagName.toLowerCase() === tagName
                 );
                 let index = siblings.indexOf(element) + 1;
                 return `${parentXPath}/${tagName}[${index}]`;
             } else {
                 throw new Error('Cannot determine XPath: Element has no attributes, no innerText, and no parent.');
             }
         }
     }

     // Add indexing if duplicates exist
//     let matchingElements = document.evaluate(xpath, document, null, XPathResult.ORDERED_NODE_SNAPSHOT_TYPE, null);
//     if (matchingElements.snapshotLength > 1) {
//         let siblings = Array.from(element.parentNode.children).filter(
//             (sibling) => sibling.tagName.toLowerCase() === tagName
//         );
//         let index = siblings.indexOf(element) + 1;
//         xpath = `(${xpath})[${index}]`;
//     }

// Evaluate to get all matching elements
        const snapshot = document.evaluate(
          xpath,
          document,
          null,
          XPathResult.ORDERED_NODE_SNAPSHOT_TYPE,
          null
        );

        let elementIndex = 0;
        for (let i = 0; i < snapshot.snapshotLength; i++) {
          if (snapshot.snapshotItem(i) === element) {
            elementIndex = i + 1;
            break;
          }
        }

        // If more than one match, add the index
        if (snapshot.snapshotLength > 1 && elementIndex > 0) {
          xpath = `(${xpath})[${elementIndex}]`;
        }

//let matchingElements = document.evaluate(xpath, document, null, XPathResult.ORDERED_NODE_SNAPSHOT_TYPE, null);
//if (matchingElements.snapshotLength > 1) {
//    // Handle duplicate element
//    let siblings = Array.from(element.parentNode.children).filter(
//        (sibling) => sibling.tagName.toLowerCase() === tagName
//    );
//    let index = siblings.indexOf(element) + 1;
//    xpath = `(${xpath})[${index}]`;
//    if(matchingElements.snapshotLength > 1) {
//    // **Handle duplicate parent**: Add the parent’s index if necessary
//    let parent = element.parentElement;
//    if (parent) {
//        let parentXPath = getRelativeXPath(parent); // Get XPath of the parent element
//        let parentSiblings = Array.from(parent.parentElement.children).filter(
//            (sibling) => sibling.tagName.toLowerCase() === parent.tagName.toLowerCase()
//        );
//        let parentIndex = parentSiblings.indexOf(parent) + 1;
//
//        // Update XPath to include parent's index
//        xpath = `${parentXPath}[${parentIndex}]/${tagName}[${index}]`;
//
//        // **Handle parent's parent duplication**: If the parent is also a duplicate, add parent's parent index
//        let grandparent = parent.parentElement;
//        if (grandparent) {
//            let grandparentXPath = getRelativeXPath(grandparent); // Get XPath of the parent's parent
//            let grandparentSiblings = Array.from(grandparent.parentElement.children).filter(
//                (sibling) => sibling.tagName.toLowerCase() === grandparent.tagName.toLowerCase()
//            );
//            let grandparentIndex = grandparentSiblings.indexOf(grandparent) + 1;
//
//            // Update XPath to include grandparent’s index
//            xpath = `${grandparentXPath}[${grandparentIndex}]/${parent.tagName.toLowerCase()}[${parentIndex}]/${tagName}[${index}]`;
//        }
//    }
//   }
//}

     return xpath;
 }
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
        var innerText = event.target.innerText || ''; // Get the innerText of the element
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
            alert('hi');
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
                placeholder: placeholderAttribute,
                innerText: innerText // Add the innerText to the details
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
