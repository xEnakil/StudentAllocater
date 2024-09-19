document.addEventListener('DOMContentLoaded', function () {
    const selects = document.querySelectorAll('.ranking-select');

    function updateOptions() {
        const selectedValues = Array.from(selects).map(select => select.value);

        selects.forEach(select => {
            const currentValue = select.value;
            select.querySelectorAll('option').forEach(option => {
                if (option.value !== "" && option.value !== currentValue) {
                    option.disabled = selectedValues.includes(option.value);
                } else {
                    option.disabled = false;
                }
            });
        });
    }

    selects.forEach(select => {
        select.addEventListener('change', updateOptions);
    });

    updateOptions();
});
