/**
 * JavaScript logic for Stock Update screen (Synopsis Figure 4).
 * Implements category-specific NA grey-out and real-time variant code generation.
 */

document.addEventListener("DOMContentLoaded", function () {
    const categorySelect = document.getElementById("categorySelect");
    const designNoInput = document.getElementById("designNoInput");
    const genderSelect = document.getElementById("genderSelect");
    const sizeSelect = document.getElementById("sizeSelect");
    const colourSelect = document.getElementById("colourSelect");
    const swimwearTypeRadios = document.querySelectorAll("input[name='swimwearType']");
    const sleeveLengthSelect = document.getElementById("sleeveLengthSelect");
    const lowerLengthSelect = document.getElementById("lowerLengthSelect");
    const variantCodeDisplay = document.getElementById("variantCodeDisplay");
    const hiddenGeneratedCode = document.getElementById("hiddenGeneratedCode");

    const APPAREL_SIZES = [
        { val: "S", label: "S - Small" },
        { val: "M", label: "M - Medium" },
        { val: "L", label: "L - Large" },
        { val: "XL", label: "XL - Extra Large" },
        { val: "XXL", label: "XXL - Double Large" },
        { val: "FS", label: "FS - Free Size" }
    ];

    const FOOTWEAR_SIZES = [
        { val: "6", label: "UK / India 6" },
        { val: "7", label: "UK / India 7" },
        { val: "8", label: "UK / India 8" },
        { val: "9", label: "UK / India 9" },
        { val: "10", label: "UK / India 10" },
        { val: "11", label: "UK / India 11" },
        { val: "12", label: "UK / India 12" }
    ];

    const ACCESSORY_SIZES = [
        { val: "FS", label: "FS - Free Size" },
        { val: "NA", label: "NA - Not Applicable" }
    ];

    function updateSizeOptions(category) {
        if (!sizeSelect) return;
        const currentVal = sizeSelect.value;
        sizeSelect.innerHTML = "";

        let optionsList = APPAREL_SIZES;
        if (category === "Footwear") {
            optionsList = FOOTWEAR_SIZES;
        } else if (category === "Accessories") {
            optionsList = ACCESSORY_SIZES;
        }

        optionsList.forEach(opt => {
            const el = document.createElement("option");
            el.value = opt.val;
            el.textContent = opt.label;
            if (opt.val === currentVal) el.selected = true;
            sizeSelect.appendChild(el);
        });

        if (!sizeSelect.value && optionsList.length > 0) {
            sizeSelect.selectedIndex = 0;
        }
    }

    function handleCategoryChange() {
        const category = categorySelect ? categorySelect.value : "Swimwear";
        const swimwearControls = document.getElementById("swimwearControlsGroup");

        updateSizeOptions(category);

        if (category === "Accessories" || category === "Footwear") {
            // Both sleeve length and lower length are NOT APPLICABLE
            if (swimwearControls) swimwearControls.style.display = "none";
            disableField(sleeveLengthSelect, "NA");
            disableField(lowerLengthSelect, "NA");
        } else {
            // Swimwear category
            if (swimwearControls) swimwearControls.style.display = "block";
            handleSwimwearSubtype();
        }

        updateGeneratedVariantCode();
    }

    function handleSwimwearSubtype() {
        const selectedRadio = document.querySelector("input[name='swimwearType']:checked");
        const subType = selectedRadio ? selectedRadio.value : "bottom";

        if (subType === "bottom") {
            // Trunks, shorts, capri, pants apply
            enableField(lowerLengthSelect);
            disableField(sleeveLengthSelect, "NA");
        } else if (subType === "top") {
            // Rash guard, tops (sleeve length applies)
            enableField(sleeveLengthSelect);
            disableField(lowerLengthSelect, "NA");
        } else {
            // One-piece or accessories: NA for both
            disableField(sleeveLengthSelect, "NA");
            disableField(lowerLengthSelect, "NA");
        }
    }

    function disableField(selectElem, valueToSet) {
        if (!selectElem) return;
        selectElem.value = valueToSet;
        selectElem.disabled = true;
        selectElem.classList.add("na-disabled");
    }

    function enableField(selectElem) {
        if (!selectElem) return;
        selectElem.disabled = false;
        selectElem.classList.remove("na-disabled");
        if (selectElem.value === "NA") {
            selectElem.selectedIndex = 0;
        }
    }

    function getEffectiveLength() {
        const category = categorySelect ? categorySelect.value : "";
        if (category === "Accessories" || category === "Footwear") {
            return "NA";
        }
        const selectedRadio = document.querySelector("input[name='swimwearType']:checked");
        const subType = selectedRadio ? selectedRadio.value : "bottom";

        if (subType === "bottom" && lowerLengthSelect) {
            return lowerLengthSelect.value || "B";
        } else if (subType === "top" && sleeveLengthSelect) {
            return sleeveLengthSelect.value || "FS";
        }
        return "NA";
    }

    function updateGeneratedVariantCode() {
        const gender = genderSelect ? genderSelect.value : "M";
        const size = sizeSelect ? sizeSelect.value : "L";
        const length = getEffectiveLength();
        const colour = colourSelect ? colourSelect.value : "BK";

        // Produces M/L/B/BK for black trunks for males in size large
        const generatedCode = `${gender}/${size}/${length}/${colour}`;

        if (variantCodeDisplay) {
            variantCodeDisplay.textContent = generatedCode;
        }
        if (hiddenGeneratedCode) {
            hiddenGeneratedCode.value = generatedCode;
        }
    }

    // Attach listeners
    if (categorySelect) categorySelect.addEventListener("change", handleCategoryChange);
    if (designNoInput) designNoInput.addEventListener("input", updateGeneratedVariantCode);
    if (genderSelect) genderSelect.addEventListener("change", updateGeneratedVariantCode);
    if (sizeSelect) sizeSelect.addEventListener("change", updateGeneratedVariantCode);
    if (colourSelect) colourSelect.addEventListener("change", updateGeneratedVariantCode);
    if (sleeveLengthSelect) sleeveLengthSelect.addEventListener("change", updateGeneratedVariantCode);
    if (lowerLengthSelect) lowerLengthSelect.addEventListener("change", updateGeneratedVariantCode);

    swimwearTypeRadios.forEach(r => {
        r.addEventListener("change", function () {
            handleSwimwearSubtype();
            updateGeneratedVariantCode();
        });
    });

    // Handle design select dropdown helper
    const existingDesignSelect = document.getElementById("existingDesignSelect");
    if (existingDesignSelect && designNoInput) {
        existingDesignSelect.addEventListener("change", function () {
            if (this.value) {
                designNoInput.value = this.value;
                const cat = this.options[this.selectedIndex].getAttribute("data-category");
                if (cat && categorySelect) {
                    categorySelect.value = cat;
                    handleCategoryChange();
                }
                updateGeneratedVariantCode();
            }
        });
    }

    // Clear form button
    const clearBtn = document.getElementById("clearStockBtn");
    if (clearBtn) {
        clearBtn.addEventListener("click", function (e) {
            e.preventDefault();
            const form = document.getElementById("stockUpdateForm");
            if (form) {
                form.reset();
                handleCategoryChange();
            }
        });
    }

    // Initial setup on page load
    handleCategoryChange();
});
