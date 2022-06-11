/*
 * Swiss QR Bill Generator
 * Copyright (c) 2018 Manuel Bleichenbacher
 * Licensed under MIT License
 * https://opensource.org/licenses/MIT
 *
 * Partially according to "Creating a custom form field control" guide
 * (https://material.angular.io/guide/creating-a-custom-form-field-control).
 * Partially a copy of MatInput.
 * Copyright Google LLC All Rights Reserved.
 */

import { BooleanInput, coerceBooleanProperty } from '@angular/cdk/coercion';
import { AutofillMonitor } from '@angular/cdk/text-field';
import {
  OnChanges,
  OnDestroy,
  DoCheck,
  Input,
  ElementRef,
  Self,
  Optional,
  Directive,
  NgZone,
  AfterViewInit,
  Inject
} from '@angular/core';
import {
  NgControl,
  NgForm,
  FormGroupDirective
} from '@angular/forms';
import {
  CanUpdateErrorState,
  ErrorStateMatcher,
  mixinErrorState
} from '@angular/material/core';
import { MatFormField, MatFormFieldControl, MAT_FORM_FIELD } from '@angular/material/form-field';
import { Platform } from '@angular/cdk/platform';
import { InputFormatter } from './input-formatter';
import { Subject } from 'rxjs';
import { MatAutocomplete } from '@angular/material/autocomplete';

let nextUniqueId = 0;

const _MatInputBase = mixinErrorState(
  class {
    /**
     * Emits whenever the component state changes and should cause the parent
     * form field to update. Implemented as part of `MatFormFieldControl`.
     * @docs-private
     */
    readonly stateChanges = new Subject<void>();
    constructor(
      public _defaultErrorStateMatcher: ErrorStateMatcher,
      public _parentForm: NgForm,
      public _parentFormGroup: FormGroupDirective,
      /**
       * Form control bound to the component.
       * Implemented as part of `MatFormFieldControl`.
       * @docs-private
       */
      public ngControl: NgControl,
    ) {}
  }
);

/**
 * Directive that allows a native input to work inside a `MatFormField` and apply a formatting when input loses focus.
 */
@Directive({
  selector: `input[qrbillInputWithFormat]`,
  exportAs: 'qrbillInputWithFormat',
  host: {
    /**
     * @breaking-change 8.0.0 remove .mat-form-field-autofill-control in favor of AutofillMonitor.
     */
    'class': 'mat-input-element mat-form-field-autofill-control',
    '[class.mat-input-server]': '_isServer',
    // Native input properties that are overwritten by Angular inputs need to be synced with
    // the native input element. Otherwise property bindings for those don't work.
    '[attr.id]': 'id',
    // At the time of writing, we have a lot of customer tests that look up the input based on its
    // placeholder. Since we sometimes omit the placeholder attribute from the DOM to prevent screen
    // readers from reading it twice, we have to keep it somewhere in the DOM for the lookup.
    '[attr.data-placeholder]': 'placeholder',
    '[disabled]': 'disabled',
    '[required]': 'required',
    '[attr.name]': 'name || null',
    '[attr.readonly]': 'readonly || null',
    // Only mark the input as invalid for assistive technology if it has a value since the
    // state usually overlaps with `aria-required` when the input is empty and can be redundant.
    '[attr.aria-invalid]': '(empty && required) ? null : errorState',
    '[attr.aria-required]': 'required',
    '(focus)': '_focusChanged(true)',
    '(blur)': '_focusChanged(false)',
    '(input)': '_onInput()',
  },
  providers: [
    { provide: MatFormFieldControl, useExisting: InputWithFormatDirective }
  ]
})
export class InputWithFormatDirective extends _MatInputBase
  implements
  MatFormFieldControl<any>,
  OnChanges,
  OnDestroy,
  AfterViewInit,
  DoCheck,
  CanUpdateErrorState {
  protected _uid = `input-with-format-${nextUniqueId++}`;
  protected _rawValue?: any;
  protected _previousNativeValue: any;
  private _previousPlaceholder: string | null = null;

  /** Whether the component is being rendered on the server. */
  readonly _isServer: boolean;

  /** Whether the input is inside of a form field. */
  readonly _isInFormField: boolean;

  /**
   * Implemented as part of MatFormFieldControl.
   * @docs-private
   */
   focused: boolean = false;

  /**
   * Implemented as part of MatFormFieldControl.
   */
  override readonly stateChanges: Subject<void> = new Subject<void>();

  /**
   * Implemented as part of MatFormFieldControl.
   */
  controlType = 'input-with-format';

  /**
   * Implemented as part of MatFormFieldControl.
   */
  autofilled = false;

  /**
   * Implemented as part of MatFormFieldControl.
   */
  @Input()
  get disabled(): boolean {
    if (this.ngControl && this.ngControl.disabled !== null) {
      return this.ngControl.disabled;
    }
    return this._disabled;
  }
  set disabled(value: BooleanInput) {
    this._disabled = coerceBooleanProperty(value);

    // Browsers may not fire the blur event if the input is disabled too quickly.
    // Reset from here to ensure that the element doesn't become stuck.
    if (this.focused) {
      this.focused = false;
      this.stateChanges.next();
    }
  }
  protected _disabled = false;

  /**
   * Implemented as part of MatFormFieldControl.
   */
  @Input()
  get id(): string {
    return this._id;
  }
  set id(value: string) {
    this._id = value || this._uid;
  }
  protected _id! : string;

  /**
   * Implemented as part of MatFormFieldControl.
   */
  @Input() placeholder: string;

  /**
   * Name of the input.
   * @docs-private
   */
   @Input() name: string;

   /**
   * Implemented as part of MatFormFieldControl.
   */
  @Input()
  get required(): boolean {
    return this._required ?? false;
  }
  set required(value: BooleanInput) {
    this._required = coerceBooleanProperty(value);
  }
  protected _required: boolean | undefined;

  /** An object used to control when error messages are shown. */
  @Input() override errorStateMatcher!: ErrorStateMatcher;

  /**
   * Implemented as part of MatFormFieldControl.
   * @docs-private
   */
  @Input('aria-describedby') userAriaDescribedBy?: string;

  /**
  * Implemented as part of MatFormFieldControl.
  */
  @Input()
  get value(): string {
    return this._rawValue!;
  }
  set value(value: any) {
    if (value !== this._rawValue) {
      this._rawValue = value;
      if (this._inputFormatter) {
        this._elementRef.nativeElement.value = this._inputFormatter.formattedValue(
          value
        );
      }
      this.stateChanges.next();
      if (this._onChange) {
        this._onChange(value);
      }
    }
  }

  /** Formatter instance */
  @Input()
  get inputFormatter(): InputFormatter {
    return this._inputFormatter;
  }
  set inputFormatter(val: InputFormatter) {
    this._inputFormatter = val;
    this._elementRef.nativeElement.value = this._inputFormatter.formattedValue(
      this._rawValue
    );
  }
  private _inputFormatter!: InputFormatter;

  /** Whether the element is readonly. */
  @Input()
  get readonly(): boolean { return this._readonly; }
  set readonly(value: BooleanInput) { this._readonly = coerceBooleanProperty(value); }
  private _readonly = false;

  /** Autocomplete element linked to the input field */
  @Input('qrBillAutoComplete')
  get autoComplete(): MatAutocomplete | undefined {
    return this._matAutoComplete;
  }
  set autoComplete(value: MatAutocomplete | undefined) {
    this._matAutoComplete = value;
  }
  private _matAutoComplete?: MatAutocomplete = undefined;


  constructor(
    protected _elementRef: ElementRef<HTMLInputElement>,
    protected _platform: Platform,
    @Optional() @Self() ngControl: NgControl,
    @Optional() _parentForm: NgForm,
    @Optional() _parentFormGroup: FormGroupDirective,
    _defaultErrorStateMatcher: ErrorStateMatcher,
    private _autofillMonitor: AutofillMonitor,
    ngZone: NgZone,
    // TODO: Remove this once the legacy appearance has been removed. We only need
    // to inject the form field for determining whether the placeholder has been promoted.
    @Optional() @Inject(MAT_FORM_FIELD) private _formField?: MatFormField) {

    super(_defaultErrorStateMatcher, _parentForm, _parentFormGroup, ngControl);

    const element = this._elementRef.nativeElement;

    this._previousNativeValue = this.value;

    // Force setter to be called in case id was not specified.
    this.id = this.id;
    this.placeholder = '';
    this.name = this.id;

    if (this.ngControl != null) {
      this.ngControl.valueAccessor = this;
    }

    // On some versions of iOS the caret gets stuck in the wrong place when holding down the delete
    // key. In order to get around this we need to "jiggle" the caret loose. Since this bug only
    // exists on iOS, we only bother to install the listener on iOS.
    if (_platform.IOS) {
      ngZone.runOutsideAngular(() => {
        _elementRef.nativeElement.addEventListener('keyup', this._iOSKeyupListener);
      });
    }

    this._isServer = !this._platform.isBrowser;
    this._isInFormField = !!_formField;
  }

  ngAfterViewInit() {
    if (this._platform.isBrowser) {
      this._autofillMonitor.monitor(this._elementRef.nativeElement).subscribe(event => {
        this.autofilled = event.isAutofilled;
        this.stateChanges.next();
      });
    }
  }

  ngOnChanges() {
    this.stateChanges.next();
  }

  ngOnDestroy() {
    this.stateChanges.complete();

    if (this._platform.isBrowser) {
      this._autofillMonitor.stopMonitoring(this._elementRef.nativeElement);
    }

    if (this._platform.IOS) {
      this._elementRef.nativeElement.removeEventListener('keyup', this._iOSKeyupListener);
    }
  }

  ngDoCheck() {
    if (this.ngControl) {
      // We need to re-evaluate this on every change detection cycle, because there are some
      // error triggers that we can't subscribe to (e.g. parent form submissions). This means
      // that whatever logic is in here has to be super lean or we risk destroying the performance.
      this.updateErrorState();
    }

    // We need to dirty-check the native element's value, because there are some cases where
    // we won't be notified when it changes (e.g. the consumer isn't using forms or they're
    // updating the value using `emitEvent: false`).
    this._dirtyCheckNativeValue();

    // We need to dirty-check and set the placeholder attribute ourselves, because whether it's
    // present or not depends on a query which is prone to "changed after checked" errors.
    this._dirtyCheckPlaceholder();
  }

  /** Focuses the input. */
  focus(options?: FocusOptions): void {
    this._elementRef.nativeElement.focus(options);
  }

  _onBlur() {
    if (!!this.autoComplete && this.autoComplete.isOpen) {
      return;
    }
    this._focusChanged(false);
  }

  _onFocus() {
    this._focusChanged(true);
  }

  /** Callback for the cases where the focused state of the input changes. */
  _focusChanged(isFocused: boolean) {
    if (isFocused !== this.focused && (!this.readonly || !isFocused)) {
      this.focused = isFocused;
      this.stateChanges.next();
      if (isFocused) {
        if (this._inputFormatter.editValue) {
          this._elementRef.nativeElement.value = this._inputFormatter.editValue(
            this._rawValue
          );
        }
      } else {
        this._previousNativeValue = this._inputFormatter.formattedValue(
          this._rawValue
        );
        this._elementRef.nativeElement.value = this._previousNativeValue;
        if (this._onTouched) {
          this._onTouched();
        }
      }
    }
  }

  _onInput() {
    // This is a noop function and is used to let Angular know whenever the value changes.
    // Angular will run a new change detection each time the `input` event has been dispatched.
    // It's necessary that Angular recognizes the value change, because when floatingLabel
    // is set to false and Angular forms aren't used, the placeholder won't recognize the
    // value changes and will not disappear.
    // Listening to the input event wouldn't be necessary when the input is using the
    // FormsModule or ReactiveFormsModule, because Angular forms also listens to input events.
  }

  /** Does some manual dirty checking on the native input `placeholder` attribute. */
  private _dirtyCheckPlaceholder() {
    // If we're hiding the native placeholder, it should also be cleared from the DOM, otherwise
    // screen readers will read it out twice: once from the label and once from the attribute.
    // TODO: can be removed once we get rid of the `legacy` style for the form field, because it's
    // the only one that supports promoting the placeholder to a label.
    const formField = this._formField;
    const placeholder =
      formField && formField.appearance === 'legacy' && !formField._hasLabel?.()
        ? null
        : this.placeholder;
    if (placeholder !== this._previousPlaceholder) {
      const element = this._elementRef.nativeElement;
      this._previousPlaceholder = placeholder;
      placeholder ?
        element.setAttribute('placeholder', placeholder) : element.removeAttribute('placeholder');
    }
  }

  /** Does some manual dirty checking on the native input `value` property. */
  protected _dirtyCheckNativeValue() {
    const newValue = this._elementRef.nativeElement.value;

    if (this._previousNativeValue !== newValue) {
      this._previousNativeValue = newValue;
      this._rawValue = this._inputFormatter.rawValue(newValue);
      this.stateChanges.next();
      if (this._onChange) {
        this._onChange(this._rawValue);
      }
    }
  }

  /** Checks whether the input is invalid based on the native validation. */
  protected _isBadInput() {
    // The `validity` property won't be present on platform-server.
    let validity = (this._elementRef.nativeElement as HTMLInputElement).validity;
    return validity && validity.badInput;
  }

  /**
   * Implemented as part of MatFormFieldControl.
   */
  get empty(): boolean {
    return !this._elementRef.nativeElement.value && !this._isBadInput() &&
      !this.autofilled;
  }

  /**
   * Implemented as part of MatFormFieldControl.
   */
  get shouldLabelFloat(): boolean {
    return this.focused || !this.empty;
  }

  /**
   * Implemented as part of MatFormFieldControl.
   */
  setDescribedByIds(ids: string[]) {
    if (ids.length) {
      this._elementRef.nativeElement.setAttribute('aria-describedby', ids.join(' '));
    } else {
      this._elementRef.nativeElement.removeAttribute('aria-describedby');
    }
  }

  /**
   * Implemented as part of MatFormFieldControl.
   */
  onContainerClick() {
    // Do not re-focus the input element if the element is already focused. Otherwise it can happen
    // that someone clicks on a time input and the cursor resets to the "hours" field while the
    // "minutes" field was actually clicked. See: https://github.com/angular/components/issues/12849
    if (!this.focused) {
      this.focus();
    }
  }

  writeValue(obj: any): void {
    this.value = obj;
  }

  private _onChange = (_: any) => { };
  registerOnChange(fn: any): void {
    this._onChange = fn;
  }

  private _onTouched = () => { };
  registerOnTouched(fn: any): void {
    this._onTouched = fn;
  }

  private _iOSKeyupListener = (event: Event): void => {
    const el = event.target as HTMLInputElement;

    // Note: We specifically check for 0, rather than `!el.selectionStart`, because the two
    // indicate different things. If the value is 0, it means that the caret is at the start
    // of the input, whereas a value of `null` means that the input doesn't support
    // manipulating the selection range. Inputs that don't support setting the selection range
    // will throw an error so we want to avoid calling `setSelectionRange` on them. See:
    // https://html.spec.whatwg.org/multipage/input.html#do-not-apply
    if (!el.value && el.selectionStart === 0 && el.selectionEnd === 0) {
      // Note: Just setting `0, 0` doesn't fix the issue. Setting
      // `1, 1` fixes it for the first time that you type text and
      // then hold delete. Toggling to `1, 1` and then back to
      // `0, 0` seems to completely fix it.
      el.setSelectionRange(1, 1);
      el.setSelectionRange(0, 0);
    }
  };
}
