/**
 * Génère une chaine de caractère alphanumérique de taille length.
 *
 * @param {string} color - La taille de la chaine de caractère à générer.
 * @returns {undefined}
 */
function addAlphaToColor(color) {
  return color.replace('rgb', 'rgba').replace(')', ', 0.08)');
}

const colorUtilss = {
  addAlphaToColor,
};

export default colorUtilss;
