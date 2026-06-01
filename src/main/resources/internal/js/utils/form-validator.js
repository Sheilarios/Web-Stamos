/**
 * form-validator.js
 * Validador genérico de formularios para STAMOS
 * Dependencias: jQuery (opcional, pero recomendado)
 */

(function($) {
    'use strict';

    // ==================== REGLAS DE VALIDACIÓN PREDEFINIDAS ====================
    const validators = {
        required: function(value) {
            if (value === undefined || value === null) return false;
            if (typeof value === 'string') return value.trim().length > 0;
            if (typeof value === 'number') return true;
            return !!value;
        },
        email: function(value) {
            const re = /^[^\s@]+@([^\s@.,]+\.)+[^\s@.,]{2,}$/;
            return re.test(String(value).toLowerCase());
        },
        ruc: function(value) {
            const str = String(value).trim();
            // RUC peruano: 11 dígitos (empresas) o 8 dígitos (persona natural con DNI)
            return /^\d{8}$/.test(str) || /^\d{11}$/.test(str);
        },
        phone: function(value) {
            const str = String(value).trim();
            // Teléfono fijo o móvil: 7-9 dígitos, puede incluir guiones o espacios
            return /^[\d\s\-+]{7,15}$/.test(str);
        },
        number: function(value) {
            return !isNaN(parseFloat(value)) && isFinite(value);
        },
        integer: function(value) {
            return /^-?\d+$/.test(String(value).trim());
        },
        min: function(value, param) {
            return parseFloat(value) >= param;
        },
        max: function(value, param) {
            return parseFloat(value) <= param;
        },
        minLength: function(value, param) {
            return String(value).length >= param;
        },
        maxLength: function(value, param) {
            return String(value).length <= param;
        },
        pattern: function(value, param) {
            const regex = new RegExp(param);
            return regex.test(String(value));
        },
        date: function(value) {
            return !isNaN(Date.parse(value));
        },
        dateAfter: function(value, paramField) {
            const valDate = new Date(value);
            const paramDate = new Date($(paramField).val());
            return valDate > paramDate;
        },
        dateBefore: function(value, paramField) {
            const valDate = new Date(value);
            const paramDate = new Date($(paramField).val());
            return valDate < paramDate;
        },
        equals: function(value, paramField) {
            return value === $(paramField).val();
        }
    };

    // ==================== GESTOR DE VALIDACIÓN POR FORMULARIO ====================
    class FormValidator {
        constructor(formElement, options = {}) {
            this.form = $(formElement);
            this.options = $.extend({
                validateOnBlur: true,
                validateOnInput: true,
                showErrorsInline: true,
                errorClass: 'is-invalid',
                successClass: 'is-valid',
                errorContainer: '.invalid-feedback',
                onSuccess: null,
                onError: null
            }, options);

            this.fields = [];
            this.init();
        }

        init() {
            // Buscar campos con atributo data-validation
            this.form.find('[data-validation]').each((i, field) => {
                const $field = $(field);
                const rules = $field.data('validation').split('|').map(r => r.trim());
                const messages = ($field.data('validation-message') || '').split('|').map(m => m.trim());
                this.fields.push({
                    $field,
                    rules,
                    messages
                });
                if (this.options.validateOnBlur) {
                    $field.on('blur', () => this.validateField($field));
                }
                if (this.options.validateOnInput) {
                    $field.on('input', () => this.validateField($field));
                }
            });

            // Validar al enviar
            this.form.on('submit', (e) => {
                if (!this.validateAll()) {
                    e.preventDefault();
                    if (this.options.onError) this.options.onError(this.getErrors());
                } else {
                    if (this.options.onSuccess) this.options.onSuccess();
                }
            });
        }

        validateField($field) {
            const fieldConfig = this.fields.find(f => f.$field.is($field));
            if (!fieldConfig) return true;

            const value = $field.val();
            let isValid = true;
            let errorMessage = '';

            for (let i = 0; i < fieldConfig.rules.length; i++) {
                const rule = fieldConfig.rules[i];
                const [ruleName, ruleParam] = rule.split(':');
                const validator = validators[ruleName];
                if (!validator) continue;

                let valid = false;
                if (ruleParam && (ruleName === 'min' || ruleName === 'max' || ruleName === 'minLength' || ruleName === 'maxLength')) {
                    valid = validator(value, parseFloat(ruleParam));
                } else if (ruleParam && (ruleName === 'equals' || ruleName === 'dateAfter' || ruleName === 'dateBefore')) {
                    valid = validator(value, ruleParam);
                } else if (ruleName === 'pattern') {
                    valid = validator(value, ruleParam);
                } else {
                    valid = validator(value);
                }

                if (!valid) {
                    isValid = false;
                    errorMessage = fieldConfig.messages[i] || this.getDefaultMessage(ruleName, ruleParam);
                    break;
                }
            }

            if (!isValid) {
                $field.addClass(this.options.errorClass).removeClass(this.options.successClass);
                if (this.options.showErrorsInline) {
                    let $error = $field.next(this.options.errorContainer);
                    if (!$error.length) $error = $('<div class="invalid-feedback"></div>').insertAfter($field);
                    $error.html(errorMessage);
                }
            } else {
                $field.removeClass(this.options.errorClass).addClass(this.options.successClass);
                if (this.options.showErrorsInline) {
                    $field.next(this.options.errorContainer).remove();
                }
            }
            return isValid;
        }

        validateAll() {
            let allValid = true;
            this.fields.forEach(field => {
                if (!this.validateField(field.$field)) allValid = false;
            });
            return allValid;
        }

        getErrors() {
            const errors = [];
            this.fields.forEach(field => {
                if (field.$field.hasClass(this.options.errorClass)) {
                    errors.push({
                        field: field.$field,
                        message: field.$field.next(this.options.errorContainer).text()
                    });
                }
            });
            return errors;
        }

        getDefaultMessage(ruleName, param) {
            const messages = {
                required: 'Este campo es obligatorio.',
                email: 'Ingrese un correo electrónico válido.',
                ruc: 'Ingrese un RUC válido (8 o 11 dígitos).',
                phone: 'Ingrese un número de teléfono válido.',
                number: 'Debe ingresar un número.',
                integer: 'Debe ingresar un número entero.',
                min: `El valor mínimo es ${param}.`,
                max: `El valor máximo es ${param}.`,
                minLength: `Debe tener al menos ${param} caracteres.`,
                maxLength: `No puede exceder los ${param} caracteres.`,
                date: 'Ingrese una fecha válida.',
                dateAfter: 'La fecha debe ser posterior al campo especificado.',
                dateBefore: 'La fecha debe ser anterior al campo especificado.',
                equals: 'Los valores no coinciden.',
                pattern: 'El formato ingresado no es válido.'
            };
            return messages[ruleName] || 'Campo inválido.';
        }
    }

    // ==================== MÉTODOS DE CLASE ====================
    function addValidator(name, fn) {
        validators[name] = fn;
    }

    // ==================== FUNCIONES GLOBALES ====================
    $.fn.validateForm = function(options) {
        return this.each(function() {
            const validator = new FormValidator(this, options);
            $(this).data('validator', validator);
        });
    };

    // Exponer utilidades globalmente
    window.FormValidator = FormValidator;
    window.addValidator = addValidator;

})(jQuery);