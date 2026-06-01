/**
 * date-helpers.js
 * Utilidades para el manejo de fechas en STAMOS
 * Independiente de Moment.js, usando JavaScript nativo (Date)
 */

// ==================== CONFIGURACIÓN LOCALIZACIÓN ====================
const DATE_HELPERS_LOCALE = {
    months: ['Enero', 'Febrero', 'Marzo', 'Abril', 'Mayo', 'Junio', 'Julio', 'Agosto', 'Septiembre', 'Octubre', 'Noviembre', 'Diciembre'],
    monthsShort: ['Ene', 'Feb', 'Mar', 'Abr', 'May', 'Jun', 'Jul', 'Ago', 'Sep', 'Oct', 'Nov', 'Dic'],
    weekdays: ['Domingo', 'Lunes', 'Martes', 'Miércoles', 'Jueves', 'Viernes', 'Sábado'],
    weekdaysShort: ['Dom', 'Lun', 'Mar', 'Mié', 'Jue', 'Vie', 'Sáb'],
    weekdaysMin: ['Do', 'Lu', 'Ma', 'Mi', 'Ju', 'Vi', 'Sá']
};

// ==================== FORMATEO DE FECHAS ====================

/**
 * Formatea una fecha a string según el patrón dado (similar a PHP date)
 * @param {Date|string} date - Fecha o string convertible
 * @param {string} format - Patrón: d-m-Y, d/m/Y, Y-m-d, etc. Soporta d, m, Y, H, i, s
 * @returns {string} Fecha formateada
 */
function formatDate(date, format) {
    const d = toDate(date);
    if (!d) return '';
    const map = {
        d: String(d.getDate()).padStart(2, '0'),
        m: String(d.getMonth() + 1).padStart(2, '0'),
        Y: d.getFullYear(),
        H: String(d.getHours()).padStart(2, '0'),
        i: String(d.getMinutes()).padStart(2, '0'),
        s: String(d.getSeconds()).padStart(2, '0')
    };
    return format.replace(/[d,m,Y,H,i,s]/g, match => map[match] || match);
}

/**
 * Formatea fecha en formato DD/MM/YYYY (ej. 31/12/2025)
 * @param {Date|string} date 
 * @returns {string}
 */
function formatDateDMY(date) {
    return formatDate(date, 'd/m/Y');
}

/**
 * Formatea fecha en formato YYYY-MM-DD (para inputs de tipo date)
 * @param {Date|string} date 
 * @returns {string}
 */
function formatDateYMD(date) {
    return formatDate(date, 'Y-m-d');
}

/**
 * Formatea fecha y hora en formato DD/MM/YYYY HH:MM
 * @param {Date|string} date 
 * @returns {string}
 */
function formatDateTime(date) {
    return formatDate(date, 'd/m/Y H:i');
}

/**
 * Obtiene el nombre del mes (Ej: "Marzo")
 * @param {number|Date} monthIndexOrDate - Mes (0-11) o fecha
 * @returns {string}
 */
function getMonthName(monthIndexOrDate) {
    let month;
    if (typeof monthIndexOrDate === 'number') {
        month = monthIndexOrDate;
    } else {
        const d = toDate(monthIndexOrDate);
        month = d ? d.getMonth() : -1;
    }
    return (month >= 0 && month <= 11) ? DATE_HELPERS_LOCALE.months[month] : '';
}

/**
 * Obtiene el nombre del día de la semana (Ej: "Lunes")
 * @param {number|Date} dayIndexOrDate - Día (0-6) o fecha
 * @returns {string}
 */
function getDayName(dayIndexOrDate) {
    let day;
    if (typeof dayIndexOrDate === 'number') {
        day = dayIndexOrDate;
    } else {
        const d = toDate(dayIndexOrDate);
        day = d ? d.getDay() : -1;
    }
    return (day >= 0 && day <= 6) ? DATE_HELPERS_LOCALE.weekdays[day] : '';
}

// ==================== CONVERSIÓN Y VALIDACIÓN ====================

/**
 * Convierte cualquier valor a objeto Date, retorna null si no es válido
 * @param {any} value - String, número timestamp o Date
 * @returns {Date|null}
 */
function toDate(value) {
    if (!value) return null;
    if (value instanceof Date) return isNaN(value.getTime()) ? null : value;
    if (typeof value === 'string') {
        // Intentar parsear formatos comunes: YYYY-MM-DD, DD/MM/YYYY, timestamp
        let parts = value.match(/^(\d{4})-(\d{2})-(\d{2})/); // YYYY-MM-DD
        if (parts) {
            return new Date(parseInt(parts[1]), parseInt(parts[2]) - 1, parseInt(parts[3]));
        }
        parts = value.match(/^(\d{2})\/(\d{2})\/(\d{4})/); // DD/MM/YYYY
        if (parts) {
            return new Date(parseInt(parts[3]), parseInt(parts[2]) - 1, parseInt(parts[1]));
        }
        // Timestamp numérico
        if (/^\d+$/.test(value)) {
            const ts = parseInt(value);
            return new Date(ts);
        }
    }
    if (typeof value === 'number') {
        return new Date(value);
    }
    return null;
}

/**
 * Verifica si una fecha es válida
 * @param {any} date 
 * @returns {boolean}
 */
function isValidDate(date) {
    const d = toDate(date);
    return d !== null && !isNaN(d.getTime());
}

// ==================== OPERACIONES CON FECHAS ====================

/**
 * Suma o resta días a una fecha
 * @param {Date|string} date - Fecha base
 * @param {number} days - Número de días (positivo = futuro, negativo = pasado)
 * @returns {Date|null}
 */
function addDays(date, days) {
    const d = toDate(date);
    if (!d) return null;
    d.setDate(d.getDate() + days);
    return d;
}

/**
 * Suma o resta meses a una fecha
 * @param {Date|string} date 
 * @param {number} months 
 * @returns {Date|null}
 */
function addMonths(date, months) {
    const d = toDate(date);
    if (!d) return null;
    d.setMonth(d.getMonth() + months);
    return d;
}

/**
 * Diferencia en días entre dos fechas (fechaFin - fechaInicio)
 * @param {Date|string} start 
 * @param {Date|string} end 
 * @returns {number} Diferencia en días
 */
function diffDays(start, end) {
    const startDate = toDate(start);
    const endDate = toDate(end);
    if (!startDate || !endDate) return NaN;
    const msDiff = endDate.getTime() - startDate.getTime();
    return Math.floor(msDiff / (1000 * 60 * 60 * 24));
}

/**
 * Compara si dos fechas son el mismo día (ignora hora)
 * @param {Date|string} a 
 * @param {Date|string} b 
 * @returns {boolean}
 */
function isSameDay(a, b) {
    const d1 = toDate(a);
    const d2 = toDate(b);
    if (!d1 || !d2) return false;
    return d1.getFullYear() === d2.getFullYear() &&
           d1.getMonth() === d2.getMonth() &&
           d1.getDate() === d2.getDate();
}

/**
 * Verifica si una fecha es fin de semana (sábado o domingo)
 * @param {Date|string} date 
 * @returns {boolean}
 */
function isWeekend(date) {
    const d = toDate(date);
    if (!d) return false;
    const day = d.getDay();
    return day === 0 || day === 6;
}

/**
 * Obtiene el número de semana del año (ISO 8601)
 * @param {Date|string} date 
 * @returns {number}
 */
function getWeekNumber(date) {
    const d = toDate(date);
    if (!d) return NaN;
    const temp = new Date(d.getFullYear(), d.getMonth(), d.getDate());
    const dayOfYear = (Date.UTC(temp.getFullYear(), temp.getMonth(), temp.getDate()) - Date.UTC(temp.getFullYear(), 0, 0)) / 86400000;
    const firstThursday = new Date(temp.getFullYear(), 0, 4);
    const weekNum = Math.ceil((dayOfYear + (firstThursday.getDay() + 6) % 7) / 7);
    return weekNum;
}

/**
 * Genera un array de fechas entre un rango (inclusive)
 * @param {Date|string} start 
 * @param {Date|string} end 
 * @returns {Date[]} Array de fechas (objetos Date)
 */
function getDateRangeArray(start, end) {
    const startDate = toDate(start);
    const endDate = toDate(end);
    if (!startDate || !endDate) return [];
    const dates = [];
    let current = new Date(startDate);
    while (current <= endDate) {
        dates.push(new Date(current));
        current.setDate(current.getDate() + 1);
    }
    return dates;
}

// ==================== TIEMPO RELATIVO (hace X días) ====================

/**
 * Retorna texto relativo (ej. "hoy", "ayer", "hace 3 días", "15/10/2025")
 * @param {Date|string} date 
 * @returns {string}
 */
function getRelativeTime(date) {
    const d = toDate(date);
    if (!d) return '';
    const today = new Date();
    today.setHours(0, 0, 0, 0);
    const target = new Date(d);
    target.setHours(0, 0, 0, 0);
    const diff = target.getTime() - today.getTime();
    const daysDiff = Math.round(diff / 86400000);
    if (daysDiff === 0) return 'hoy';
    if (daysDiff === -1) return 'ayer';
    if (daysDiff === 1) return 'mañana';
    if (daysDiff < 0) return `hace ${-daysDiff} días`;
    if (daysDiff > 0) return `en ${daysDiff} días`;
    return formatDateDMY(date);
}

// ==================== EXPORTACIÓN (si se usa módulos) ====================
// Si el proyecto usa módulos ES6, se puede exportar. Para uso global se deja en window.
window.dateHelpers = {
    formatDate,
    formatDateDMY,
    formatDateYMD,
    formatDateTime,
    getMonthName,
    getDayName,
    toDate,
    isValidDate,
    addDays,
    addMonths,
    diffDays,
    isSameDay,
    isWeekend,
    getWeekNumber,
    getDateRangeArray,
    getRelativeTime
};