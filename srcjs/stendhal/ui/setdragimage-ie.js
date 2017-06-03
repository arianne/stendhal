/*
The MIT License (MIT)

Copyright (c) 2014 Mihai Valentin

Permission is hereby granted, free of charge, to any person obtaining a copy
of this software and associated documentation files (the "Software"), to deal
in the Software without restriction, including without limitation the rights
to use, copy, modify, merge, publish, distribute, sublicense, and/or sell
copies of the Software, and to permit persons to whom the Software is
furnished to do so, subject to the following conditions:

The above copyright notice and this permission notice shall be included in all
copies or substantial portions of the Software.

THE SOFTWARE IS PROVIDED "AS IS", WITHOUT WARRANTY OF ANY KIND, EXPRESS OR
IMPLIED, INCLUDING BUT NOT LIMITED TO THE WARRANTIES OF MERCHANTABILITY,
FITNESS FOR A PARTICULAR PURPOSE AND NONINFRINGEMENT. IN NO EVENT SHALL THE
AUTHORS OR COPYRIGHT HOLDERS BE LIABLE FOR ANY CLAIM, DAMAGES OR OTHER
LIABILITY, WHETHER IN AN ACTION OF CONTRACT, TORT OR OTHERWISE, ARISING FROM,
OUT OF OR IN CONNECTION WITH THE SOFTWARE OR THE USE OR OTHER DEALINGS IN THE
SOFTWARE.
 
global DataTransfer, DragEvent */

/*! setDragImage-IE - polyfill for setDragImage method for Internet Explorer 10+
 https://github.com/MihaiValentin/setDragImage-IE */

/**
 * this method preloads the image, so it will be already loaded when we will use it as a drag image
 * @param image
 */
window.setDragImageIEPreload = function(image) {
    var bodyEl,
        preloadEl;

    bodyEl = document.body;

    // create the element that preloads the  image
    preloadEl = document.createElement('div');
    preloadEl.style.background = 'url("' + image.src + '")';
    preloadEl.style.position = 'absolute';
    preloadEl.style.opacity = 0.001;

    bodyEl.appendChild(preloadEl);

    // after it has been preloaded, just remove the element so it won't stay forever in the DOM
    setTimeout(function() {
        bodyEl.removeChild(preloadEl);
    }, 5000);
};

// if the setDragImage is not available, implement it
if ('function' !== typeof DataTransfer.prototype.setDragImage) {
    DataTransfer.prototype.setDragImage = function(image, offsetX, offsetY) {
        var randomDraggingClassName,
            dragStylesCSS,
            dragStylesEl,
            headEl,
            parentFn,
            eventTarget;

        // generate a random class name that will be added to the element
        randomDraggingClassName = 'setdragimage-ie-dragging-' + Math.round(Math.random() * Math.pow(10, 5)) + '-' + Date.now();

        // prepare the rules for the random class
        dragStylesCSS = [
            '.' + randomDraggingClassName,
            '{',
            'background: url("' + image.src + '") no-repeat #fff 0 0 !important;',
            'width: ' + image.width + 'px !important;',
            'height: ' + image.height + 'px !important;',
            'text-indent: -9999px !important;',
            'border: 0 !important;',
            'outline: 0 !important;',
            '}',
            '.' + randomDraggingClassName + ' * {',
            'display: none !important;',
            '}'
        ];
        // create the element and add it to the head of the page
        dragStylesEl = document.createElement('style');
        dragStylesEl.innerText = dragStylesCSS.join('');
        headEl = document.getElementsByTagName('head')[0];
        headEl.appendChild(dragStylesEl);

       	eventTarget = window.event.target;

        // and add the class we prepared to it
        eventTarget.classList.add(randomDraggingClassName);

        /* immediately after adding the class, we remove it. in this way the browser will
        have time to make a snapshot and use it just so it looks like the drag element */
        setTimeout(function() {
            // remove the styles
            headEl.removeChild(dragStylesEl);
            // remove the class
            eventTarget.classList.remove(randomDraggingClassName);
        }, 0);
    };
}