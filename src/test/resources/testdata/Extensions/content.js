
							if (!window.localStorage.getItem('capturedEvents')) {
                            window.localStorage.setItem('capturedEvents', JSON.stringify([]));
                        }
                        var getAttributes = function(element) {
                            var attributes = {};
							if (element.hasAttributes()) {
                            var attributeNames = element.getAttributeNames();
                            attributeNames.forEach(function(name) {
                                attributes[name] = element.getAttribute(name);
                            });
							}
                            return attributes;
                        };
                        function getUniqueCssSelector(element) {
                            if (!(element instanceof Element)) {
                                throw new Error('Provided argument is not an element.');
                            }
                            console.log('element is ', element);
                            console.log('node type is ', element.nodeType, Node.ELEMENT_NODE);
                            var path = [];
                            while (element.nodeType === Node.ELEMENT_NODE) {
                                var selector = element.nodeName.toLowerCase();
                                console.log('selector is ', selector);
                                if (element.id) {
                                    selector += '#' + element.id;
                                    path.unshift(selector);
                                    break;
                                } else {
                                    if (element.className) {
                                       var classNames = element.className.trim().replace(/\s+/g, '.');
                                        selector += '.' + classNames;
                                        console.log('class selector is ', selector);
                                    }
                                    var sibling = element;
                                    var nth = 1;
                                    console.log('prev sibling bef is ', sibling.previousElementSibling);
                                    while ((sibling = sibling.previousElementSibling)) {
                                        console.log('prev sibling after is ', sibling.previousElementSibling);
                                        nth++;
                                    }
                                    console.log('n is ', nth);
                                    selector += ':nth-child(' + nth + ')';
                                    path.unshift(selector);
                                    if (document.querySelectorAll(path.join(' > ')).length === 1)
                                       break;
                                    element = element.parentNode;
                                }
                            }
                            return path.join(' > ');
                        }
                        var getUniqueLocators = function(element, id, className, attributes, name, valueAttrib) {
                            var ignoreAttributes = ['onclick', 'onload', 'onchange', 'onmouseover', 'onfocus'];
                            if (id && document.getElementById(id) !== null) return ['id', id];
                            if (name && document.getElementsByName(name).length == 1) return ['name', name];
                            try {
                                var cssSelector = getUniqueCssSelector(element);
                                console.log('css is ', cssSelector);
                                if (document.querySelectorAll(cssSelector).length === 1) return ['cssSelector', cssSelector];
                            } catch (e) {
                                console.log('Css selector is not valid');
                            }
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
                            console.log('xpath1 is ', xpath1);
                            if (document.evaluate(xpath1, document, null, XPathResult.ORDERED_NODE_SNAPSHOT_TYPE, null).snapshotLength === 1) {
                                return ['xpath', xpath1];
                            }
                            return ['none', ''];
                        };
                        var logEvent = function(eventType, event) {
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
                            var events = JSON.parse(window.localStorage.getItem('capturedEvents'));
                            console.log('event logging started', event);

                            events.push({
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
                            });
                            console.log('event logged', event);
							console.log('events ', events);
							console.log(window.localStorage.capturedEvents);
							try {
                            window.localStorage.setItem('capturedEvents', JSON.stringify(events));
							} catch (e) {
								console.log(e);
							}
                        };
                        document.addEventListener('click', function(event) {
                            logEvent('Click', event);
                        }, true);
                        document.addEventListener('dblclick', function(event) {
                            logEvent('Double Click', event);
                        }, true);
                        document.addEventListener('select', function(event) {
                            logEvent('Select', event);
                        }, true);
                        document.addEventListener('toggle', function(event) {
                            logEvent('Toggle', event);
                        }, true);
                        document.addEventListener('change', function(event) {
                            logEvent('Change', event);
                        }, true);
                        document.addEventListener('hashchange', function(event) {
                            logEvent('Hashchange', event);
                        }, true);
						document.addEventListener('load', function(event) {
                            logEvent('Load', event);
                        }, true);




                        function getCapturedEvents() {
                            var capturedEvents = JSON.parse(window.localStorage.getItem('capturedEvents'));
                            return capturedEvents.map(function(event) {
                                return {
                                    details: event.details
                                };
                            });
                        }